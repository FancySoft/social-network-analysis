Что с этим делать:

Класс Transform - вспомогательный, он помогает загрузить в экземпляр класса NameMatcher данные по именам
Класс NameMatcher состоит из двух ArrayList<ArrayList<String>> - для женских и мужских имен, например
[ [Татьяна, Таня, Танька], [Мария, Маша, Машка] ]
[ [Александр, Саша, Саня], [Алексей, Леха, Леша, Лешка] ]

Конструктор: NameMatcher(String femalePath, String malePath), где femalePath - путь к файлу femaleNamesSet.txt,
								  malePath - путь к файлу maleNamesSet.txt,
где, собственно, и содержатся наборы форм имен

у класса 3 открытых метода:
public boolean match(String name1, String name2) - возвращает true, если это одно и то же имя
public boolean match(String name1, String name2, int sex) - возвращает true, если это одно и то же имя,
где sex = 1 - female, sex = 2 - male, else он ищет, как будто пол не указан
public ArrayList<String> allForms(String name, int sex) - возвращает все возможные формы имени или null, если такого имени в списке нет

примеры использования: 
NameMatcher nm = new NameMatcher("d:\\...\\femaleNamesSet.txt", d:\\...\\maleNamesSet.txt");

nm.match("Александр", "Саша"); - true
nm.match("Александра", "Саша"); - true
nm.match("Александр", "Леша"); - false
nm.match("Татьяна", "Танька"); - true
nm.match("Александр", "Саша", 1); - false (ищем только по женским именам)
nm.match("Александра", "Саша", 3); - true

nm.allForms("Саша", 1).toString(); - [Александра, Саша, Шура etc]
nm.allForms("Саша", 2).toString(); - [Александр, Саша, Шура etc]