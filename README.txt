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
Run terminal, change directory to the project dir, 
and run "ant" from there.

----------------------------------------------------
	Run
----------------------------------------------------
java -jar Crawler.jar [path]
[path] - text file with a list of IDs to parse

Text file example:
id1
id2
id3

Usage examples:
-- Crawler ../accounts/parsing_list.txt (you should use that)
-- Crawler accounts/parsing_list.txt (value by default)


java -jar Randomizer.jar [path_from] [path_to]
[path_from] - a set of .xml-files storing the AccountVectors 
from the social network, that we want to randomize for tests
[path_to] - an output directory

Usage:
-- Randomizer ../accounts/vk ../accounts/social_network2

Note: folder social_network2 should be created


java -jar Matcher.jar [path_from] [path_to]
[path_from] - accounts from the first social network
[path_to] - from the second
The first set will be matched on the second set.

Usage:
-- Matcher ../accounts/vk ../accounts/social_network2

