��� � ���� ������:

����� Transform - ���������������, �� �������� ��������� � ��������� ������ NameMatcher ������ �� ������
����� NameMatcher ������� �� ���� ArrayList<ArrayList<String>> - ��� ������� � ������� ����, ��������
[ [�������, ����, ������], [�����, ����, �����] ]
[ [���������, ����, ����], [�������, ����, ����, �����] ]

�����������: NameMatcher(String femalePath, String malePath), ��� femalePath - ���� � ����� femaleNamesSet.txt,
								  malePath - ���� � ����� maleNamesSet.txt,
���, ����������, � ���������� ������ ���� ����

� ������ 3 �������� ������:
public boolean match(String name1, String name2) - ���������� true, ���� ��� ���� � �� �� ���
public boolean match(String name1, String name2, int sex) - ���������� true, ���� ��� ���� � �� �� ���,
��� sex = 1 - female, sex = 2 - male, else �� ����, ��� ����� ��� �� ������
public ArrayList<String> allForms(String name, int sex) - ���������� ��� ��������� ����� ����� ��� null, ���� ������ ����� � ������ ���

������� �������������: 
NameMatcher nm = new NameMatcher("d:\\...\\femaleNamesSet.txt", d:\\...\\maleNamesSet.txt");

nm.match("���������", "����"); - true
nm.match("����������", "����"); - true
nm.match("���������", "����"); - false
nm.match("�������", "������"); - true
nm.match("���������", "����", 1); - false (���� ������ �� ������� ������)
nm.match("����������", "����", 3); - true

nm.allForms("����", 1).toString(); - [����������, ����, ���� etc]
nm.allForms("����", 2).toString(); - [���������, ����, ���� etc]