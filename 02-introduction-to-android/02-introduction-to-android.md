# Введение в Android

## Оглавление

- [Скачивание и установка Android Studio](#cкачивание-и-установка-android-studio)
- [Создание первого приложения](#создание-первого-приложения)
- [Установка эмулятора](#установка-эмулятора)
- [Подключение реального устройства](#подключение-реального-устройства)
- [Запуск приложения на эмуляторе и устройстве](#запуск-приложения-на-эмуляторе-и-устройстве)
- [Структура проекта](#структура-проекта)
- [Приложение `Dice Roller`](#приложение-dice-roller)
  - [Добавление кнопки](#добавление-кнопки)
  - [Обработка нажатия на кнопку](#обработка-нажатия-на-кнопку)
  - [Добавление изображения костей](#добавление-изображения-костей)
- [Сборка и управление зависимостями](#сборка-и-управление-зависимостями)

## Скачивание и установка Android Studio

Android Studio — специальная среда разработки под Android. Основана на IntelliJ IDEA, поэтому пользовательский интерфейс очень похож.

Скачать Android Studio можно с официального сайта для разработчиков под Android: developer.android.com: https://developer.android.com/studio.

Устанавливается стандартным образом для каждой из поддерживаемых операционных систем (Windows, Linux, macOS).  
Инструкцию можно найти здесь: https://developer.android.com/studio/install.

## Создание первого приложения

Для создания нового проекта необходимо выполнить следующие шаги:

Запустить Android Studio -> "Start a new Android Studio project".  
Choose your project -> Empty Activity -> Next.  

Configure your project ->  
Name: Dice Roller  
Package: `com.example.android.diceroller`  
Project location: путь до каталога с проектом в файловой системе  
Language: Kotlin  
Minimum API: минимальный уровень API (версия Android)  
-> Finish

Проект нового приложения создан. Для его запуска необходимо либо подключить реальное Android-устройство, либо установить и запустить эмулятор.

## Установка эмулятора

Для установки эмулятора необходимо выполнить следующие шаги в рамках Android Studio:

Tools -> AVD Manager -> Create Virtual Device.  
Choose a device definition -> Pixel -> Next.  
Select a system image -> Pie -> Next.  
Verify Configuration -> AVD Name -> pixel2-api28 -> Finish.  

Эмулятор создан. Можно его запустить, нажав на кнопку "Run" в окне "AVD Manager".

## Подключение реального устройства

Для подключения реального устройства необходимо: 
1. Включить отладку по USB:
  1. Включить режим разработчика: Настройки -> О телефоне -> Номер сборки (нажать 5 раз). Шаги могут отличаться в зависимости от модели устройства и версии Android. В настройках появится пункт "Для разработчика".
  2. Перейти в раздел настроек "Для разработчиков" и включить "Отладка по USB".
2. Подключить устройство к компьютеру по кабелю.
3. Подтвердить разрешение на отладку по USB на устройстве.

## Запуск приложения на эмуляторе и устройстве

Для запуска приложения необходимо сперва выбрать устройство (слева от кнопки "Run"), на котором будет выполнен запуск, а затем нажать кнопку "Run".

## Структура проекта

Изучим структуру проекта приложения.  
Вкладка "Project" на панели слева отображает структуру проекта. Режим "Android" не отображает реальную структуру проекта, но он удобен для разработки. Режим "Project" отображает реальную структуру проекта в файловой системе.

Рассмотрим структуру проекта в режиме "Android".  
На верхнем уровне проект отображает свои модули и список Gradle-скриптов. Проект может содержать более, чем один модуль, но по-умолчанию у него есть хотя бы один модуль `app` с исходным кодом приложения. Gradle-скрипты — это сборочные скрипты проекта, описывающие этапы сборки, зависимости и другую необходимую для сборки проекта информацию. О них позже.

Модуль `app` содержит разделы:
* `manifests` — содержит файл манифеста приложения `AndroidManifest.xml`,
* `java` — содержит исходный код приложения на языке Java или Kotlin,
* `res` — содержит ресурсы приложения, такие как изображения, иконки, переводимые строки, файлы разметки интерфейса и др.

Манифест `AndroidManifest.xml` располагается в корневом каталоге проекта. Он содержит важную информацию о приложении, которая требуется системе Android для выполнения какого-либо кода приложения. Среди прочего файл манифеста выполняет следующее:

* Задает имя пакета для приложения. Имя пакета служит его уникальным идентификатором в системе Android и в магазине Google Play.
* Описывает различные компоненты приложения: активности (Activities), службы (Services), провайдеры контента (Content Providers) и др. На основании этих описаний система Android может определить, из каких компонентов состоит приложение и при каких условиях их можно использовать.
* Объявляет какие разрешения приложение может запрашивать у пользователя, чтобы приложение могло получить доступ к защищенным API системы. Например, это могут быть разрешения на чтение контактов, текущего местоположения пользователя, или доступ к файловой системе.
* Объявляет минимальный уровень API (версию) Android, который требуется приложению.

Раздел (или каталог) `java` содержит исходный код приложения. При создании нового проекта с пустой активностью создается лишь один файл исходного кода — класс `MainActivity`, содержащий код единственного экрана приложения. Класс `MainActivity` по-умолчанию содержит лишь один переопределенный метод `onCreate()`, наследованный от класса `AppCompatActivity`.

```java
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
```

Вызов `setContentView()` как ясно из названия метода, устанавливает контент для отображения на экране для данной активности. В качестве параметра в метод передается ссылка на ресурс с описанием разметки экрана: `activity_main.xml`.

Ресурсы с разметкой экранов располагаются в каталоге `layout`. Это могут быть ресурсы с разметкой не только экранов, но каких-либо других UI-элементов.  
Кроме этого среди ресурсов могут содержаться изображения (`drawable`), иконки запуска (`mipmap`), определения цветов (`values/colors.xml`), переводимые строки (`values/strings.xml`), определения тем и стилей элементов приложения (`values/styles.xml`).

Список Gradle-скриптов содержит конфигурационные файлы Gradle `build.gradle`. Один файл принадлежит всему проекту и описывает конфигурацию сборки проекта: репозитории зависимостей, задача очистки проекта. Второй файл принадлежит модулю `app` и описывает конфигурацию сборки конкретного модуля: описание параметров системы Android, зависимости от библиотек. Если проект содержит более, чем один модуль, то и Gradle-файлов он содержит больше — по одному на каждый модуль.

## Приложение `Dice Roller`

В качестве простейшего примера создадим приложение "Dice Roller", которое будет иметь один единственный экран с изображением игрального кубика и кнопкой для имитации броска.

Заготовка для приложения уже создана. Далее необходимо:
1. Добавить кнопку для броска.
2. Добавить обработчик нажатия на кнопку, который будет описывать действия необходимые для имитации броска.
3. Добавить изображения игрального кубика.

Готовый пример располагается в каталоге рядом с данным конспектом.

### Добавление кнопки

Игральный кубик содержат точки в диапазоне от 1 до 6. Таким образом кнопка для имитации броска должна генерировать случайное число от 1 до 6.

Сперва рассмотрим добавление кнопки без добавления изображения, а для проверки работы кнопки необходимо подготовить текстовое поле для отображения числа от 1 до 6.

**1. Изменение текста и его размера на экране (activity_main.xml):**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    tools:context=".MainActivity">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_horizontal"
        android:textSize="30sp"
        android:text="1" />

</LinearLayout>
```

**2. Добавление кнопки (тега `Button`):**

```xml
<Button
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_gravity="center_horizontal"
    android:text="@string/roll" />
```

Параметр `layout_gravity=center_horizontal` позволяет установить кнопку в центре по горизонтали.  
Параметр `text` содержит ссылку на строковый ресурс с именем `roll`. Ресурс содержит строку "Roll".

**3. Расположение элементов в центре экрана:**

Для расположения элементов в центре экрана необходимо добавить следующие параметры к тегу `LinearLayout`.  
Параметр `layout_gravity="center_vertical"` располагает компонент `LinearLayout` в центре по вертикали экрана.  
Параметр `orientation` определяет ориентацию расположения элементов внутри `LinearLayout`.

```xml
android:layout_gravity="center_vertical"
android:orientation="vertical"
```

### Обработка нажатия на кнопку

Для того, чтобы нажатие на кнопку броска имело какой-нибудь эффект, необходимо добавить для кнопки обработчик события нажатия на нее. Для этого необходимо задать уникальный идентификатор для кнопки, чтобы можно было получить ее экземпляр, и установить обработчик события `onClick`.

**1. Установка идентификатора кнопки:**

```xml
android:id="@+id/roll_button"
```

**2. Получение экземпляра кнопки по ее идентификатору:**

```java
val rollButton: Button = findViewById(R.id.roll_button)
```

**3. Установка обработчика события `onClick`:**

```java
rollButton.setOnClickListener {
    val randomInt = Random().nextInt(6) + 1
    val resultText: TextView = findViewById(R.id.result_text)
    resultText.text = randomInt.toString()
}
```

Обработчик нажатия на кнопку описывает генерацию случайного числа от 1 до 6, получение экземпляра текстового поля по идентификатору `result_text` (предварительно идентификатор должен быть добавлен к компоненту `TextView`), и установку сгенерированного числа в качестве текста.

Если запустить приложение, можно убедиться, что нажатие на кнопку "Roll" меняет значение числа на экране.

### Добавление изображения костей

Для завершения приложение не хватает отображения картинки с игральным кубиком вместо обычного числа. Для добавления изображения необходимо: добавить заранее подготовленные файлы изображений в каталог ресурсов `drawable`, заменить компонент для отображения текста `TextView` на компонент `ImageView`, обновить обработчик нажатия на кнопку "Roll" для смены изображения в зависимости от сгенерированного числа.

**1. Добавление изображений в каталог `drawable`:**

Скачать файлы с изображениями игральной кости по ссылке https://github.com/udacity/andfun-kotlin-dice-roller/raw/master/DiceImages.zip, и добавить файлы в каталог `res/drawable`.

**2. Замена `TextView` на `ImageView`:**

```xml
<ImageView
    android:id="@+id/dice_image"
    android:layout_width="wrap_content"
	android:layout_height="wrap_content"
    android:layout_gravity="center_horizontal"
    android:src="@drawable/empty_dice" />
```

Компонент `ImageView` устанавливает изображение с пустым кубиком в параметре `src`. Для `ImageView` установлен идентификатор `dice_image`.

**3. Замена получения экземпляра `TextView` на `ImageView`:**

```java
val diceImage: ImageView = findViewById(R.id.dice_image)
```

**4. Обновление обработчика нажатия на кнопку "Roll":**

По нажатию на кнопку "Roll" необходимо отобразить картинку игрального кубика со сгенерированным числом точек. Для этого необходимо сперва определить правильную ссылку на изображение среди ресурсов с помощью конструкции `when`. После этого необходимо установить полученную ссылку на ресурс для объекта `ImageView` с помощью метода `setImageResource()`.

```java
rollButton.setOnClickListener {
    val drawableRes = when (Random.nextInt(1, 6)) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        else ->  R.drawable.dice_6
    }
    diceImage.setImageResource(drawableRes)
}
```

## Сборка и управление зависимостями

Сборкой Android-приложений занимается утилита Gradle. Кроме сборки Gradle отвечает за следующее:

* Определяет какие устройства, могут запустить данное приложение.
* Компилирует код в исполняемый файл приложения.
* Управляет зависимостями приложения.
* Подписывает приложения специальными ключами для публикации в Google Play.
* Собирает и запускает автоматические тесты.

Главная задача Gradle — сборка проекта. Gradle в процессе сборки компилирует исходный код (`*java`- и `*kt`-файлы), берет файлы ресурсов, скомпилированный код, манифест (`AndroidManifest.xml`), внешние использующиеся библиотеки, если они есть, и упаковывает все эти файлы в один APK-файл (Android Application Package) — исполняемый формат файлов для распространения Android-приложений.

В каждом проекте располагается как минимум два конфигурационных файла Gradle. Каждый обладает именем `build.gradle`. Один из файлов располагается в корне проекта и является конфигурационным файлом всего проекта. Второй располагается в корне модуля `app` и является соответственно конфигурационным файлов модуля. Если проект содержит больше чем 1 модуль, то и Gradle-файлов модулей больше — по одному на каждый модуль.

Gradle-файл проекта определяет репозитории и зависимости общие для всех модулей в проекте.  
Репозитории — хранилища, в которых будут искаться добавляемые зависимости.  
Зависимости — библиотеки или инструменты, которые необходимы для работы проекта.

Также часть конфигурации проекта располагается в файле `settings.gradle`.

Код ниже демонстрирует файл проекта `settings.gradle`, создающийся по-умолчанию. Здесь описаны репозитории `google()`, `mavenCentral()` и `gradlePluginPortal()`, использующиеся для доступа к плагинам и зависимостям во всех модулях проекта. 

```gradle
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "My Application"
include ':app'
```

Блок `pluginManagement {}` описывает список репозиториев для загрузки плагинов для использования в рамках проекта.  
Блок `dependencyResolutionManagement {}` описывает список репозиториев для загрузки библиотек зависимостей для использования в рамках проекта и его модулей.
Также здесь описывается имя проекта `rootProject.name` и список модулей. В данном примере лишь один модуль `:app`, но если бы их было больше, они бы были перечислены через запятую.

Пример файла `build.gradle` проекта описывает объявления плагинов для сборки и разработки.

```gradle
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id 'com.android.application' version '7.4.0' apply false
    id 'com.android.library' version '7.4.0' apply false
    id 'org.jetbrains.kotlin.android' version '1.8.20-Beta' apply false
}
```

Пример `build.gradle` для модуля `app` представлен ниже.

```gradle
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
}

android {
    namespace 'com.example.myapplication'
    compileSdk 33

    defaultConfig {
        applicationId "com.example.myapplication"
        minSdk 24
        targetSdk 33
        versionCode 1
        versionName "1.0"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = '1.8'
    }
    buildFeatures {
        viewBinding true
    }
}

dependencies {
    implementation 'androidx.core:core-ktx:1.7.0'
    implementation 'androidx.appcompat:appcompat:1.4.1'
    implementation 'com.google.android.material:material:1.5.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.3'
    implementation 'androidx.navigation:navigation-fragment-ktx:2.4.1'
    implementation 'androidx.navigation:navigation-ui-ktx:2.4.1'
    implementation 'androidx.core:core-ktx:+'
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.3'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.4.0'
}
```


Блок `plugins` описывает плагины, которые должны быть включены в проект:

* `com.android.application` — объявляет проект как Android-приложение.
* `kotlin-android` — включает использование языка Kotlin для Android-проекта.

В более старых версиях Gradle плагины добавлялись не целым блоком, а каждый отдельно с помощью директивы `apply plugin`. Например,

```
apply plugin: 'com.android.application'
```

Блок `android` описывает параметры сборки проекта под Android: 
* `namespace` — уникальный идентификатор пакета приложения для публикации в системе Android.
* `compileSdk` — уровень Android API для компиляции. Должно совпадать с `targetSdkVersion`.
* `minSdk` — минимальный уровень Android API требуемый для работы приложения.
* `targetSdk` — целевой уровень Android API требуемый для работы приложения.
* `versionCode` — кодовое число версии приложения. Используется для идентификации версии приложения в системе Android.
* `versionName` — текстовое название версии приложения. Версия, которая показывается пользователю.
* `buildTypes` — блок, описывающий параметры сборки и цифровой подписи приложения.
* `dependencies` — блок, описывающий зависимости от библиотек, необходимых проекту. Зависимости задаются идентификаторами библиотек в репозиториях, включенных в Gradle-файле проекта.
