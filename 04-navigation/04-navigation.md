# Навигация в приложении

## Оглавление

- [Введение](#введение)
- [Fragments](#fragments)
- [Реализация навигации](#реализация-навигации)
	- [Добавление нового фрагмента](#добавление-нового-фрагмента)
	- [Добавление перехода от одного экрана к другому](#добавление-перехода-от-одного-экрана-к-другому)
	- [Манипуляции со стеком фрагментов](#манипуляции-со-стеком-фрагментов)
- [ActionBar-меню](#actionbar-меню)
- [SafeArgs](#safeargs)
- [Navigation Drawer](#navigation-drawer)
- [Интенты](#интенты)
  - [Explicit-интенты](#explicit-интенты)
  - [Implicit-интенты](#implicit-интенты)
  - [Добавление функции "Share" с использованием интента](#добавление-функции-share-с-использованием-интента)

## Введение

Для работы с навигацией между экранами в Android-приложении применяется библиотека **Navigation Component**. Она входит в набор Android Jetpack и помогает реализовать навигацию, от простых нажатий на кнопки до более сложных, таких как панели приложений (action bars) и панель навигации (navigation drawers).

Библиотека предоставляет ряд преимуществ:
* Корректная обработка кнопок «Вверх» и «Назад» по умолчанию
* Поведение по умолчанию для анимации и переходов.
* Реализация шаблонов навигации пользовательского интерфейса (таких, как navigation drawer).
* Безопасность типов при передаче информации во время навигации.
* Инструменты Android Studio для визуализации и редактирования navigation flow приложения.

В качестве примера будет использоваться готовый проект, в котором уже есть все необходимые экраны, их макеты и исходные коды. Необходимо будет лишь разобраться с навигацией между экранами.

Тестовое приложение представляет собой мини-викторину. Оно будет иметь несколько экранов:
* Стартовый экран с логотипом и кнопкой "Play".
* Экран с вопросами и вариантами ответа.
* Экран с кнопкой "Try Again" в случае ошибки.
* Экран с кнопкой "Next Match" в случае успешного прохождения теста.
* Экран "About" с информацией о приложении.
* Экран "Rules" с текстом правил приложения.

Стартовый проект с приложением содержит все эти экраны, но не содержит связей между ними, их создание будет рассмотрено далее. Каждое описанный экран — это фрагмент. Фрагмент — особый компонент системы Android. Прежде чем начать разбираться с навигацией по фрагментам, нужно разобраться с тем, что это такое.

## Fragments

Фрагмент (класс `Fragment`) предоставляет часть пользовательского интерфейса в активности (`Activity`). Активности могут содержать как несколько фрагментов внутри себя, так и один фрагмент. Обычно несколько фрагментов в одной активности располагают при разработке приложения для планшетов, когда для меню навигации и для основного контента выделяются отдельные фрагменты, располагающиеся на экране одновременно.

![](fragments.png)

Фрагменты имеют свой отдельный жизненный цикл и самостоятельно отдельно от активностей занимаются обработкой событий ввода.

Обычно фрагменты имеют собственные макеты (layouts), описывающие элементы пользовательского интерфейса. Если в активностях инициализация и установка макетов выполняется в методе `onCreate()`, то в фрагментах — в методе `onCreateView()` общем для всех фрагментов. Вместо метода `setContentView()` для установки макета здесь используется метод `inflate()`, принимающий на вход ссылку на ресурс с макетом, объект `container`, являющийся родительским видом и булево значение, определяющее нужно ли новый макет привязать к корневому (в большинстве случаев используется `false`).

```kotlin
class ExampleFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, 
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.example_fragment, container, false)
    }
}
```

Для добавления фрагмента в активность необходимо описать тег `<fragment>` в макете активности.

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="horizontal"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    <fragment
            android:name="com.example.news.ExampleFragment"
            android:id="@+id/example_fragment"
            android:layout_weight="1"
            android:layout_width="0dp"
            android:layout_height="match_parent" />
</LinearLayout>
```

Существует подклассы фрагментов: `ListFragment`, `DialogFragment`, `PreferenceFragment`, `WebViewFragment` и др., упрощающие реализацию и работу с конкретными типами окон: списками, диалоговыми окнами, настройками и веб-содержимым, соответственно.

Именно фрагменты чаще используются в качестве компонентов, описывающих экраны приложения, располагаясь в одной или паре активностей, в зависимости от задач приложения. Фрагменты позволяют настраивать боковые панели навигации (navigation drawer) и ActionBar-меню. Фрагменты упрощают вопрос навигации и передачи данных между экранами приложения. Также создание фрагментов — наиболее выгодно с точки зрения траты ресурсов системы, чем создание активностей.

## Реализация навигации

Перейдем к вопросам реализации навигации между экранами приложения.

Прежде всего необходимо выделить три основные принципа навигации:
1. Приложение всегда должно иметь стартовый экран. Стартовый экран — тот, который показывается при запуске приложения.
2. Пользователь должен всегда иметь возможность вернуться назад. Это подразумевает как возврат на один экран назад с помощью системной кнопки "Назад", так и с помощью аналогичной кнопки в Action Bar.
3. Системная кнопка "Назад" и кнопка "Назад" на Action Bar должны работать аналогичным образом. Исключение — нажатие на системную кнопку на стартовом экране сворачивает приложение и возвращает пользователя на рабочий экран Android.

Сперва необходимо скачать и собрать стартовый проект **trivia-starter-code**. Стартовый проект уже содержит почти все необходимые фрагменты (кроме одного), все необходимые макеты, создавать их не нужно. Необходимо сосредоточиться исключительно на навигации.

### Добавление нового фрагмента

Единственный фрагмент, которого не хватает в стартовом проекте — это фрагмент с начальным экраном приложения `TitleFragment`. Для его создания необходимо выполнить следующие шаги:

1. File -> New -> Fragment -> Fragment (Blank).
2. Fragment Name: `TitleFragment`.
3. Убрать галочки "Create layout XML?", "Include fragment factory methods?" и "Include interface callbacks?" Макет для этого фрагмента уже есть в стартовом проекте.
4. Нажать "Finish".

Далее необходимо заменить код, сгенерированный автоматически для настройки макета фрагмента (`return TextView(activity).apply`), на использование метода `DataBindingUtil.inflate()`. Это позволит нам и инициализировать макет для фрагмента и использовать в дальнейшем Data Binding.

Определение метода `onCreateView()` должно выглядеть следующим образом:

```kotlin
override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                          savedInstanceState: Bundle?): View {
    val binding = DataBindingUtil.inflate<FragmentTitleBinding>(
    	inflater, R.layout.fragment_title, container, false)
    return binding.root
}
```

И в конце необходимо поместить фрагмент с начальным экраном на `MainActivity`, чтобы его можно было отобразить. В файл `activity_main.xml`:

```xml
<fragment
    android:id="@+id/titleFragment"
    android:name="com.example.android.navigation.TitleFragment"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

После запуска приложения на экране отобразится содержимое `TitleFragment` — начальный экран с логотипом и кнопкой "Play". По нажатию на кнопку ничего не выполняется.

### Добавление перехода от одного экрана к другому

Для реализации непосредственно навигации между экранами необходимо добавить зависимость от библиотеки **Navigation Component**, и создать файл с графом навигации.

**1. Добавление зависимости Navigation Components:**

Для добавления зависимости необходимо сперва добавить переменную `version_navigation` в блок `ext` в Gradle-файл проекта. Далее эта переменная будет использоваться для добавления зависимости конкретной версии, избавляя от дублирования номера версии в нескольких местах.

```gradle
buildscript {
    ext {
        ...
        version_navigation = "2.9.6"
        ...
    }
}
```

Добавление непосредственно зависимостей в Gradle-файл модуля `app`:

```gradle
dependencies {
    ...
    implementation "androidx.navigation:navigation-fragment-ktx:$version_navigation"
    implementation "androidx.navigation:navigation-ui-ktx:$version_navigation"
}
```

**2. Добавление графа навигации:**

Граф навигации — файл с описанием экранов приложения и связей между ними в формате XML.  
Для создания файла навигации необходимо кликнуть правой кнопкой на модуль `app` и выбрать **New** -> **Android resource file**.  
В качестве имени файла ресурса необходимо указать `navigation.xml`, а также выбрать тип `Navigation`.  
Когда файл будет создан, его можно будет найти в каталоге `res/navigation`.

**3. Замена `TitleFragment` на `NavHostFragment` в активности:**

Для реализации навигации также необходимо добавить `NavHostragment` — контейнер, который будет информацию о пунктах назначения на графе навигации. Класс `NavHostragment` является стандартным. Чтобы его использовать, необходимо в описании макета `activity_main.xml` заменить использование `TitleFragment` на `NavHostFragment`.

```xml
<fragment
   android:id="@+id/navHostFragment"
   android:name="androidx.navigation.fragment.NavHostFragment"
   android:layout_width="match_parent"
   android:layout_height="match_parent"
   app:navGraph="@navigation/navigation"
   app:defaultNavHost="true" />
```

Параметр `name` ссылается на полное имя класса `NavHostFragment`.  
Параметр `navGraph` ссылается на ресурс с описанием графа навигации, откуда класс `NavHostFragment` будет брать информацию о связях между фрагментами с экранами.  
Параметр `defaultNavHost` определяет, что этот контейнер навигации является контейнером по умолчанию и именно к нему будет применено использование системной кнопки "Назад".

**4. Добавление фрагментов на граф навигации:**

Для редактирования навигации используется редактор навигации. Он так же как и редактор макетов имеет два режима (две вкладки): **Design** и **Code**. Первый позволяет использовать элементы интерфейса IDE для редактирования графа навигации, второй предоставляет доступ к исходному XML-файлу графа. Кроме этого еще есть вкладка **Split**, которая делит рабочую область на две части: в одной код, во второй — графическое представление.

Для добавления фрагмента на граф навигации необходимо в режиме **Design** необходимо нажать на кнопку `+` в верхнем левом углу и среди макетов выбрать необходимый, например, `fragment_title`. Миниатюра фрагмента появится в рабочей зоне графа навигации.  
Далее необходимо сделать `TitleFragment` стартовым. Для этого необходимо нажать на кнопку **Assign start destination**, когда фрагмент выбран, либо указать `id` фрагмента в качестве атрибута **Start Destination** (`app:startDestination` в XML-коде) для самого графа навигации.

Если запустить приложение, то будет отображен `TitleFragment`, находящийся внутри `NavHostFragment`, который в свою очередь располагается внутри одной единственной активности.

Далее необходимо добавить еще один фрагмент, чтобы установить переход от начального экрана к следующему. Добавим на граф навигации `fragment_game`. Миниатюра макета также появляется в рабочей зоне графа навигации.  

Для установки перехода необходимо протянуть связь от фрагмента `TitleFragment` (индикатора в виде "кружочка") к `GameFragment`, и установить обработчик нажатию на кнопку "Play" на `TitleFragment`. По нажатию на эту кнопку должен выполниться переход к основному экрану игры `GameFragment`. 

Реализация обработчика будет выглядеть следующим образом: 

```kotlin
binding.playButton.setOnClickListener {
    Navigation.findNavController(it).navigate(R.id.action_titleFragment_to_gameFragment)
}
```

Статический метод `Navigation.findNavController()` служит для получения экземпляра класса `NavController`, который является контроллером для графа навигации, предоставляет различную информацию о графе навигации и позволяет взаимодейстовать с ним. Так `NavController` имеет метод `navigate()`, выполняющий переход от одного экрана к другому по уникальному идентификатору, который генерируется автоматически при создании связи. Например, идентификатор `action_titleFragment_to_gameFragment` означает переход от `titleFragment` к `gameFragment` (эти идентификаторы описаны в элементах `<fragment>` на графе навигации).

Помимо начального и основного игрового экрана приложение имеет еще экран победы `GameWonFragment` и экран поражения `GameOverFragment`. Их также необходимо добавить на граф навигации, установить переход к ним от `GameFragment`. Затем в классе `GameFragment` в методе `onCreateView()` необходимо добавить код для выполнения перехода к фрагментам в зависимости от определенных условий (места для вставки кода выделены комментариями).

```kotlin
// We've won!  Navigate to the gameWonFragment.
view.findNavController().navigate(R.id.action_gameFragment_to_gameWonFragment)
```

```kotlin
// Game over! A wrong answer sends us to the gameOverFragment.
view.findNavController().navigate(R.id.action_gameFragment_to_gameOverFragment)
```

Теперь после запуска приложения и успешного прохождения теста будет выполнен переход к `GameWonFragment`, а в случае ошибки `GameOverFragment`.

### Манипуляции со стеком фрагментов

Каждый новый открытый фрагмент помещается на стек фрагментов. Например, если пользователь прошел по всем экранам приложения от начального до экрана "Gave Over", то стек фрагментов содержит `TitleFragment`, `GameFragment` и `GameOverFragment`. Последовательность именно такая.

Если пользователь на экране "Game Over" нажмет на системную кнопку "Назад", то вернется к экрану с игрой, где будет написан последний вопрос, а это является некорректным поведением. Для пользователя логичным было бы попасть на начальный экран, а для этого необходимо удалить фрагмент `GameFragment` из стека фрагментов. Такое поведение можно реализовать следующим образом.

**1. Настройка поведения системной кнопки "Назад":**

Чтобы нажатие на системную кнопку "Назад" выполняло переход от `GameOverFragment` к `TitleFragment`, необходимо настроить атрибут **Pop Behavior**. Для этого нужно выбрать уже созданную связь между `GameFragment` и `GameOverFragment`, на панели атрибутов выбрать **Pop Behavior** -> **popUpTo** -> **gameFragment** и установить галочку **popUpToInclusive**. Настройка "поведения" определяет к какому фрагменту необходимо вернуться назад по выбранной связи (все фрагменты выше выбранного на стеке будут удалены), а галочка **popUpToInclusive** говорит, что сам фрагмент `GameFragment` тоже нужно удалить со стека (это вернет пользователя на фрагмент `TitleFragment`).

Аналогичное поведение необходимо реализовать и возврата с фрагмента `GameWonFragment` к `TitleFragment`.

**2. Добавление обработчика для кнопок на `GameOverFragment` и `GameWonFragment`:**

На экранах "Game Over" и "Congratulations" есть кнопки "Try Again" и "Next Match" соответственно. Эти кнопки должны позволять пользователю начать игру заново, т.е. возвращать пользователя к предыдущему экрану с вопросами `GameFragment`.

Для этого необходимо протянуть новую связь от `GameOverFragment` к `GameFragment` и на панели атрибутов установить **Pop To** -> **titleFragment**. Здесь указывается возврат к **titleFragment** для того, чтобы в любой момент, когда показан фрагмент `GameOverFragment` можно было вернуться к `TitleFragment`. Иначе, на стеке может казаться множество `GameOverFragment` и по кнопке "Back" возврат будет происходить именно к ним.   
Затем необходимо перейти в класс `GameOverFragment` и установить обработчик кнопки "Try Again".

```kotlin
binding.tryAgainButton.setOnClickListener {
    it.findNavController().navigate(R.id.action_gameOverFragment_to_gameFragment)
}
```

Аналогично необходимо создать связь между `GameWonFragment` и `GameFragment`, настроить **Pop Behavior** и установить обработчик нажатия на кнопку "Match Again".

**3. Добавление кнопки "Назад" на Action Bar:**

Кроме системной кнопки "Назад" Android поддерживает еще и кнопку "Назад", отображающуюся на Action Bar — панели сверху приложения. Для ее добавления необходимо внести изменения в `MainActivity`. Необходимо получить экземпляр `NavController` и использовать его для установки Action Bar:

```kotlin
private lateinit var navController: NavController

override fun onCreate(savedInstanceState: Bundle?) {
    ...

    navController = this.findNavController(R.id.navHostFragment)

    NavigationUI.setupActionBarWithNavController(this, navController)
}
```

Далее необходимо переопределить метод активности `onSupportNavigateUp()`:

```kotlin
override fun onSupportNavigateUp(): Boolean {
    return navController.navigateUp()
}
```

Метод `onSupportNavigateUp()` определяет, что именно должно быть выполнено при навигации с помощью кнопки "Назад". В данном случае это навигация по стеку с помощью экземпляра `NavController`.

Если запустить приложение, можно убедиться, что кнопка "Назад" на Action Bar отображается и работает она аналогично системной кнопке "Назад.

## ActionBar-меню

Action Bar может содержать собственное меню. Обычно меню помечается кнопкой в виде трех вертикальных точек.  
В случае приложения "Trivia" меню будет содержать один пункт "About", открывающий отдельный экран с информацией о приложении. Фрагмент `AboutFragment` уже создан.

**1. Добавление фрагмента `AboutFragment` на граф навигации:**

Для добавления возможности перехода к экрану "About" необходимо сперва добавить его на граф навигации.

**2. Создание нового ресурса `menu`:**

Конфигурация меню описывается в специальных ресурсах `menu`. Для его добавления необходимо кликнуть правой кнопкой по модулю `app`, выбрать **New** -> **Android Resource File**. Далее необходимо указать тип **Menu** и имя файла, например, **overflow_menu** и кликнуть "Ok". В результате будет создан файл `overflow_menu.xml` и помещен в каталог `res/menu`.

В меню необходимо добавить новый элемент **Menu Item**.  
На панели атрибутов указать:  
* **ID**: `aboutFragment` — совпадает с `id` фрагмента, к которому необходимо перейти при выборе данного пункта меню (ВАЖНО, чтобы эти идентификаторы совпадали),
* **title**: `@string/about` — ссылка на строковый ресурс.

**3. Добавление меню на Action Bar фрагмента `TitleFragment`:**

Для добавления меню на Action Bar фрагмента `TitleFragment` необходимо сперва добавить вызов метода `setHasOptionsMenu(true)` внутри метода `onCreateView()`. Вызов метода определяет, что данный фрагмент в принципе может отображать меню.

```kotlin
override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                         savedInstanceState: Bundle?): View {
   ...
   setHasOptionsMenu(true)
   return binding.root
}
```

Для инициализации и установки меню из файла ресурса необходимо переопределить метод фрагмента `onCreateOptionsMenu()` и описать в нем загрузку меню из файла `overflow_menu`.

```kotlin
override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.overflow_menu, menu)
}
```

**4. Реализация обработчика нажатия на элемент меню:**

Чтобы нажатие на элемент меню "About" имело эффект, необходимо реализовать обработчик нажатия.  
Для этого требуется переопределить метод фрагмента `onOptionsItemSelected()`.

```kotlin
override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return NavigationUI.onNavDestinationSelected(item, requireView().findNavController())
            || super.onOptionsItemSelected(item)
}
```

Метод `NavigationUI.onNavDestinationSelected()` выполняет навигацию к фрагменту по идентификатору меню. Именно для этого идентификаторы фрагмента и меню, по которому к фрагменту нужно перейти, важно указывать одинаковыми.

По сути фрагмент `AboutFragment` имеет в графе навигации идентификатор `aboutFragment`. Добавленный пункт меню "About" имеет аналогичный идентификатор и метод `NavigationUI.onNavDestinationSelected()`, принимающий на вход элемент меню (`MenuItem`) и экземпляр `NavController` сопоставляет эти идентификаторы и выполняет переход, если это возможно. Если переход невозможен, то метод вернет `false` и будет вызвана родительская реализация метода `super.onOptionsItemSelected(item)`.

## SafeArgs

Иногда перед разработчиком может встать задача передачи данных между экранами приложения и для этого в Android есть удобный механизм. **SafeArgs** — это Gradle-плагин, позволяющий реализовать передачу данных между фрагментами.

Далее будет рассмотрено добавление SafeArgs для простейшей задачи — отображения текстового сообщения об общем числе вопросов и числе правильных ответов на `GameWonFragment`.

**1. Добавление зависимости SafeArgs:**

Сперва необходимо добавить зависимость от библиотеки SafeArgs в Gradle-файл проекта (не модуля `app`).

```gradle
dependencies {
    ...
    classpath "androidx.navigation:navigation-safe-args-gradle-plugin:$version_navigation"
}
```

Затем необходимо включить плагин в Gradle-файл модуля `app`.

```gradle
apply plugin: "androidx.navigation.safeargs.kotlin"
```

После включения плагина необходимо очистить и пересобрать проект, чтобы компилятор сгенерировал "Directions"-классы, содержащие информацию о навигации между фрагментами (по типу идентификаторов `action_gameFragment_to_gameWonFragment`), а также позволяющие организовать передачу данных между фрагментами.

**2. Использование "Directions"-классов:**

После того, как "Directions"-классы сгенерированы, можно заменить использование ссылок на идентификаторы навигации на использование этих классов.

```kotlin
// GameFragment: Using directions to navigate to the GameWonFragment
view.findNavController().navigate(GameFragmentDirections.actionGameFragmentToGameWonFragment())
```

```kotlin
// GameFragment: Using directions to navigate to the GameOverFragment
view.findNavController().navigate(GameFragmentDirections.actionGameFragmentToGameOverFragment())
```

По такому же принципу выполняется замена в остальных местах.

**3. Добавление аргументов в редакторе навигации:**

Далее необходимо описать для `GameWonFragment` данные, которые он должен получать. В данном случае — это общее число вопросом `numQuestions` и число правильных ответов `numCorrect`. Оба числа целочисленные (`integer`).

Для добавления описания параметров необходимо перейти в редактор навигации, выбрать `gameWonFragment` и добавить аргументы в разделе "Arguments" панели "Attributes".

После этого сборка проекта будет невозможно, поскольку вызов `GameFragmentDirections.actionGameFragmentToGameWonFragment()` будет требовать входных параметров, которые только что были описаны.

**4. Передача данных фрагменту:**

Чтобы передать данные фрагменту, необходимо передать их в вызов `GameFragmentDirections.actionGameFragmentToGameWonFragment()`.

```kotlin
// GameFragment: Adding the parameters to the Action
view.findNavController().navigate(
    GameFragmentDirections.actionGameFragmentToGameWonFragment(numQuestions, questionIndex))
```

Параметры `numQuestions` и `questionIndex` определяют общее число вопросов и индекс текущего вопроса, а в случае с выигрышем — это и число правильных ответов.

Теперь проект успешно собирается, данные передаются, но никак не принимаются и не обрабатываются.

**5. Обработка полученных данных:**

Переданные в `GameWonFragment` данные отобразим на экране с помощью `Toast`-сообщения. Для этого необходимо воспользоваться вызовом `GameWonFragmentArgs.fromBundle(arguments)` для получения передаваемых данных.  
"Args"-класс `GameWonFragmentArgs` генерируется, так же как и "Directions"-классы, автоматически. Он предоставляет доступ к хранилищу `Bundle` куда сохраняются передаваемые данные.  
Далее для получения конкретных значений необходимо обратиться к данным по именам ранее переданных переменных. 

```kotlin
// GameWonFragment.onCreate

val args = GameWonFragmentArgs.fromBundle(requireArguments())
Toast.makeText(context, "NumCorrect: ${args.numCorrect}, NumQuestions: ${args.numQuestions}",
               Toast.LENGTH_LONG).show()
```

Таким образом при открытии `GameWonFragment` на экране будет отображено `Toast`-сообщение с полученными `numCorrect` и `numQuestions`.

## Navigation Drawer

Еще одним из основных элементов для навигации между экранами является боковое меню, также оно называется Navigation Drawer.  
Далее будут рассмотрены шаги по добавлению бокового меню.

**1. Добавление зависимости от библиотеки Material:**

Navigation Drawer является частью библиотеки Material, поэтому ее необходимо включить в Gradle-файл:

```gradle
implementation "com.google.android.material:material:1.13.0"
```

**2. Добавление XML-файла бокового меню:**

Далее необходимо добавить XML-файл ресурса с описанием элементов бокового меню, аналогично тому, как это было сделано для ActioBar-меню. Файл будет называться `navdrawer_menu.xml` и будет содержать один элемент "About".
Элемент меню должен иметь следующие атрибуты:  
* **ID**: `aboutFragment` — совпадает с идентификатором фрагмента, который должен быть открыт,
* **Name**: `@string/about` — ссылка на строковый ресурс,
* **Icon**: `@drawable/android` — ссылка на ресурс с изображением, ресурс уже содержится в проекте.

**3. Добавление бокового меню на макет `MainActivity`:**

Для возможность использования Navigation Draver необходимо добавить компонент `DrawerLayout` в активность и поместить внутрь этого компонента весь контент активности.

```xml
<androidx.drawerlayout.widget.DrawerLayout
    android:id="@+id/drawerLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">

        <fragment
            android:id="@+id/navHostFragment"
            android:name="androidx.navigation.fragment.NavHostFragment"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            app:navGraph="@navigation/navigation"
            app:defaultNavHost="true" />
    </LinearLayout>
</androidx.drawerlayout.widget.DrawerLayout>
```

И затем добавить компонент `NavigationView` сразу после определения `LineatLayout`, внутри которого находится фрагмент.

```xml
<com.google.android.material.navigation.NavigationView
    android:id="@+id/navView"
    android:layout_width="wrap_content"
    android:layout_height="match_parent"
    android:layout_gravity="start"
    app:headerLayout="@layout/nav_header"
    app:menu="@menu/navdrawer_menu" />
```

Параметр `headerLayout` определяет какой макет использовать в качестве заглавной части меню.  
В параметре `menu` задается ссылка на ресурс с меню, которое необходимо загрузить в `NavigationView`.

**4. Инициализация и настройка Navigation Drawer:**

Для инициализации и настройки работы Navigation Drawer необходимо сперва создать поле `drawerLayout` и инициализировать его в методе `onCreate()`:

```kotlin
class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    
    override fun onCreate(savedInstanceState: Bundle?) {
        ...
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        drawerLayout = binding.drawerLayout
        ...
    }
}
```

Для отображения кнопки бокового меню на Action Bar необходимо добавить экземпляр `drawerLayout` в качестве третьего параметра вызова `NavigationUI.setupActionBarWithNavController()`:

```kotlin
NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
```

Для добавления возможности навигации, т.е. перехода к экрану "About" при нажатии на соответствующие элемент бокового меню, необходимо добавить вызов `NavigationUI.setupWithNavController()` с передачей `NavController` и элемента `NavigationView` в качестве параметров:

```kotlin
NavigationUI.setupWithNavController(binding.navView, navController)
```

Для того, чтобы корректно обрабатывалось и открытие бокового меню и кнопка "Назад", которая отображается при переходе к следующим за начальным экранам, необходимо добавит в обработчике `onSupportNavigateUp()` в вызов `NavigationUI.navigateUp()` параметры `navController` и `drawerLayout`.

```kotlin
override fun onSupportNavigateUp(): Boolean {
    return NavigationUI.navigateUp(navController, drawerLayout)
}
```

В этом случае `drawerLayout` будет использоваться, если пользователь находится на последнем фрагменте в стеке. А `navController` будет использоваться для навигации в остальных случаях.

## Интенты

Интент — это объект, содержащий в себе запрос на выполнение некоторого действия.  
Интент может включать в себя следующие компоненты:
* действие,которое необходимо выполнить (обязательный компонент);
* набор категорий, позволяющих группировать действия;
* URI, идентифицирующий данные, над которыми необходимо выполнить действие;
* дополнительные параметры (extras), необходимые для выполнения действия.

Наиболее распространённый сценарий использования интентов — запуск другой активности. При этом это может быть как запуск активности собственного приложения, так и стороннего, например, открытие галереи, приложения почты или камеры и т.д.

Существует два вида интентов: явные (explicit) и неявные (implicit).

### Explicit-интенты

Explicit-интенты используются для перехода к активности в рамках собственного приложения. Они требуют указания полного имени класса для выполнения действия

```kotlin
val intent = Intent(MainActivity.this, SecondActivity::class.java)
startActivity(intent)
```

Первый параметр в конструкторе класса `Intent` — ссылка на контекст (в данном случае на активность, из которой выполняется вызов).  
Второй параметр — полное имя класса второй активности, которую необходимо открыть.

Также при переходе к другой активности можно поместить в интент некоторые данные.

```kotlin
val intent = Intent(MainActivity.this, SecondActivity::class.java)
intent.putExtra("KEY", "VALUE")
startActivity(intent)
```

Для получения значения из интента внутри `SecondActivity` необходимо вызвать:

```kotlin
val value = getIntent().extras["KEY"]
```

### Implicit-интенты

Implicit-интенты используются для открытия сторонних активностей сторонних приложений и выполнения каких-либо действий.  
Примеры: открытие приложения почты для написания нового письма и передача в него определенного текста; открытие приложения камеры для создания снимка и получения его в рамках приложения; открытие браузера с сайтом по определенной ссылке.


Для создания implicit-интента необходимо указать три параметра:
* **action** — действие, которое необходимо выполнить,
* **category** — категория компонента (активности), который должен выполнить действие интента,
* **data** — передаваемые данные.

Примеры констант **ACTION**:
* `ACTION_CALL` — инициализирует обращение по телефону.
* `ACTION_SEARCH` — запускает активность для выполнения поиска.
* `ACTION_SEND` — запускает активность экран для отправки данных, указанных в поле **data** интента. Например, диалог для выбора приложения, в которое необходимо использовать в качестве "отправителя".
* `ACTION_VIEW` — Наиболее распространённое общее действие. Используется для отображения какой-либо активности. Например, для открытия браузера.

Примеры констант **CATEGORY**:
* `BROWSABLE` — активность может быть вызвана браузером, чтобы отобразить "ссылочные"" данные, например, изображение или почтовое сообщение.
* `LAUNCHER` — активность может быть главной активностью, использующейся для запуска приложения в системе. Такую категорию имеет главная активность каждого приложения в Android.
* `HOME` — активность отображает Home Screen, домашний экран, который пользователь видит после включения устройства и загрузки системы, или когда нажимает клавишу "HOME".

Пример ниже демонстрирует создание интента для открытия ссылки и запуск активности, которая подойдет для выполнения действия данного интента. При наличии нескольких браузеров, пользователю будет предложено выбрать один из них.

```kotlin
val address: Uri = Uri.parse("http://developer.android.com")
val intent = Intent(Intent.ACTION_VIEW, address)
startActivity(intent)
```

Следующий пример демонстрирует создание интента для отправки некоторого текста.
В качестве действия (action) указывается `ACTION_SEND`, что при вызове `startAction()` позволит показать пользователю окно с возможностью выбора приложения для отправки сообщения (почтовые клиенты или мессенджеры).  
Сообщение сохраняется в интенте под ключом `EXTRA_TEXT`. Это стандартный ключ системы Android, именно он используется приложениями, которые могут принимать сообщения извне для дальнейшей отправки ([EXTRA_TEXT](https://developer.android.com/reference/android/content/Intent.html#EXTRA_TEXT)).

```kotlin
val shareIntent = Intent(Intent.ACTION_SEND)
shareIntent.setType("text/plain")
shareIntent.putExtra(Intent.EXTRA_TEXT, "This is a text to send via any sender")
startActivity(shareIntent)
```

### Добавление функции "Share" с использованием интента

Для демонстрации работы implicit-интентов будет добавлен функционал "Поделиться" с возможностью отправки данных, через сторонние приложения типа почтового клиента или мессенджера. В качестве сообщения будет использоваться добавленное ранее сообщения о количестве правильных ответов и общем числе вопросов.

Кнопка "Share" будет добавлена в ActionBar-меню `GameWonFragment`. В проекте уже есть файл `winner_menu.xml`, содержащий описание меню. 

**1. Настройка отображения меню на `GameWonFragment`:**

```
// GameWonFragment.onCreate

binding.nextMatchButton.setOnClickListener { view: View ->
    view.findNavController().navigate(
        GameWonFragmentDirections.actionGameWonFragmentToGameFragment())
}
setHasOptionsMenu(true)
```

**2. Добавление метода для создания интента:**

Для создания интента добавим метод `getShareIntent()`. Метод будет получать экземпляр `Bundle` с переданными данными (`numCorrect` и `numQuestions`) и создавать интент с действием `ACTION_SEND`, текстовым типом данных и строкой с самими данными.

```kotlin
// Creating our Share Intent
private fun getShareIntent() : Intent {
    val args = GameWonFragmentArgs.fromBundle(requireArguments())
    val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.setType("text/plain")
            .putExtra(Intent.EXTRA_TEXT, getString(R.string.share_success_text,
                                                   args.numCorrect, args.numQuestions))
    return shareIntent
}
```

**3. Обработка нажатия на меню "Share":**

Для обработки нажатия необходимо переопределить метод `onOptionsItemSelected` и по нажатию на пункт меню "Share" вызывать системную активность, передавая ей созданный интент.

```kotlin
override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
        R.id.share -> startActivity(getShareIntent())
    }
    return super.onOptionsItemSelected(item)
}
```

После запуска приложения можно убедиться, что по нажатию на пункт меню "Share" запускается системная активность, предлагающая выбрать приложение для отправки текстовых данных.

Если убрать установку типа "text/plain" для интента и запустить приложение снова, то после нажатия на пункт меню "Share" приложение упадет с сообщением о том, что активность для данного действия не найдена.

**4. Добавление проверки на доступность активности:**

Для того, чтобы решить проблему с падением, необходимо добавить проверку на доступность существующей активности для созданного интента. Если активности необходимой для интента нет в системе, то пункт меню "Share" логично будет скрыть.

```kotlin
override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.winner_menu, menu)

    // check if the activity resolves
    if (null == getShareIntent().resolveActivity(requireActivity().packageManager)) {
        // hide the menu item if it doesn't resolve
        menu.findItem(R.id.share).isVisible = false
    }
}
```

Если запустить приложение, можно убедиться, что пункт меню "Share" не отображается на экране `GameWonFragment`.
