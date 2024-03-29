# Проект QuickCoffee

Приложение для выбора кофейни поблизости от пользователя и заказа кофе по меню выбранной кофейни.

Для определения расстояния до пользователя используется геолокация. Есть возможность увидеть ближайшие кафе на карте. У пользователя при запуске приложения запрашиваеются соответствующие разрешения.

Первый экран приложения - экран регистрации. Почта проходит валидацию по стандартному шаблону. Пароль и повтор пароля должны быть равны и не пустые.
Данные при регистрации отправляются на сервер. Если такой пользователь уже зарегистрирован, выводится сообщение, что регистрация не удалась. Если данные введены не полностью, то поля подсвечиваются красным.

Второй экран - это экран входа по ранее зарегистриванным почте и паролю. Запрашиваются с сервера.

На третьем экране отображаются ближайшие кофейни. Происходит это в два этапа: вначале появляются данные ближайших кафе, полученные с сервера, затем, по мере получения данных о местоположении устройства, отображаются расстояния до кафе.
Пока данные о расстояниях не отобразились на экране, вместе со списком кафе отображается индикатор проресса.

По нажатию на каждое кафе в списке можно перейти на экран меню соответствующего кафе. Либо можно нажать на кнопку "На карте" и откроется окно с картой, на которой будут расположены маркеры ближайший кафе. Используются карты OSMdroid. При коротком нажитии на маркер отображается название кафе, по длинному нажатию - откроется экран меню соответствующего кафе.

На экране меню в два столбца вертикально отображается список товаров с изображениями, ценами и количеством для заказа. Выбрав хотя бы один товар можно, нажав кнопку "Перейти к оплате", перейти на экран заказа. Если ничего не выбрано, то появится соответствующее сообщение и переход не произойдёт.

На экране заказа отображается список выбранных товаров, их количество и стоимость с учётом количества. Если количество товара обнулить, то строка удаляется. Если заказ не нулевой, то можно оплатить заказ, нажав кнопку "Оплатить". Если заказ стал нулевым, то появится соответствующее сообщение.

Разработал на основе технического задания и дизайн-макета в Figma:
[макет в Figma](https://www.figma.com/file/6cPIhgzqvsZmT86IbseCoM/Coffe-17.06?type=design&node-id=0-1&mode=design&t=W1S4oOuSBASMWIui-0).

Использованные в проекте технологии:

- Coroutines;
- паттерн MVVM;
- Hilt;
- Navigation;
- Retrofit;
- JSON;
- Glide.

<img src="https://github.com/MaksimPronenko/QuickCoffee/assets/105295402/ec008a90-5249-49da-a85d-33b27ca3923b" width="270" height="585">
<img src="https://github.com/MaksimPronenko/QuickCoffee/assets/105295402/0ee097e7-ed47-4da7-8799-766bec21c68c" width="270" height="585">
<img src="https://github.com/MaksimPronenko/QuickCoffee/assets/105295402/7391116c-796e-40ff-9316-109342c6b543" width="270" height="585">
<img src="https://github.com/MaksimPronenko/QuickCoffee/assets/105295402/5998f633-a282-4fca-a93b-037717fc4010" width="270" height="585">
<img src="https://github.com/MaksimPronenko/QuickCoffee/assets/105295402/7cae4f51-93a4-437e-8fd1-4478877f768f" width="270" height="585">
<img src="https://github.com/MaksimPronenko/QuickCoffee/assets/105295402/148fe221-27fb-4115-ba81-538c0915be25" width="270" height="585">
<img src="https://github.com/MaksimPronenko/QuickCoffee/assets/105295402/a707a30c-3e3a-4d04-a75e-828cd6aee64d" width="270" height="585">
<img src="https://github.com/MaksimPronenko/QuickCoffee/assets/105295402/6f3041db-b158-439f-a57b-13a1b42e9126" width="270" height="585">
<img src="https://github.com/MaksimPronenko/QuickCoffee/assets/105295402/f2889535-10cc-4113-b4b0-6e705e14ddc9" width="270" height="585">


## Инструкция по запуску приложения

Приложение написано на Android Studio + gradle.

1. Открыть проект на GitHub, нажать кнопку "Code" и скопировать ссылку HTTPS.
   
2. Открыть папку, в которую будем клонировать приложение.
   
3. Открыть GitBash, набрать "git clone", вставить скопированную ссылку, нажимем "Enter", дождаться завершения процесса клонирования.
  
4. Запустить Android Studio. Выбрать "File", затем в выпавшем списке нажать "Open", выбрать клонированное приложение, в той папке, где его разместили. Нажать "OK". Приложение начнёт загружаться: внизу справа появится индикатор прогресса.

5. По мере загрузки справа снизу появятся сообщения с рекомендациями об обновлениях. Принять их и обновить.
   
6. Когда всё загрузилось, нажать Device Manager. Затем нажать "Create Virtual Device" (значок "+").
   
7. По умолчанию открывается вкладка "Phone". В ней выбрать устройство, которое нужноэмулировать. Например, средний по размеру "Pixel 5". Можно выбрать и другой телефон, приложение сделано для устройства любого размера.

8. Далее выбрать API Level. Например, API 34 "UpsideDownCake". В правой части окна могут появиться рекомендации. Например, может появиться сообщение "HAXM is not installed. Install HAXM" (речь идёт о механизме виртуализации для компьютеров на базе процессоров Intel). Установить. Если появляется окно с предложением выбрать объём оперативной памяти для эмулятора, выбрать рекомендованный. Другие рекомендации тоже нужно исполнить.

9. Если SDK Component для нужного уровня API не установлен, запустить его установку, нажав кнопку справа от имени API. По завершении утсановки нажать "Next".

10. В открывшемся окне выбрать Startap Orientation Portrait. Нажать "Finish".

11. В верхней части окна Andoio появится имя созданного виртуального устройства. Правее него располагается кнопка запуска приложения (зелёная стрелка вправо). Нажать её. В правой нижней части окна Android Studio появится индикация прогресса загрузки. Первая загрузка приложения на новый эмулятор может занять несколько минут. По завершении приложение откроется на эмуляторе.
