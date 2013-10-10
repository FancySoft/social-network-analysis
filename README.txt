----------------------------------------------------
	Pre-requirements
----------------------------------------------------
You must have $JAVA_HOME environment variable set up
to build succecfully.
authentication_config.txt should be created in config/
directory. It contents should be as follows:
vk_login
vk_password

To run facebook parser you should save pages with url like
'https://www.facebook.com/username/about' and
'https://www.facebook.com/username/friends_all'
as "user_info.htm" and "user_friends.htm" in
test_account_info_for_parser folder

----------------------------------------------------
	Build
----------------------------------------------------
Just use ant.

----------------------------------------------------
	Run
----------------------------------------------------
java -jar Crawler.jar [path]
path - text file with a list of IDs to parse

Text file example:
id1
id2
id3

Usage examples:
-- Crawler
-- Crawler accounts/parsing_list.txt (value by default)
