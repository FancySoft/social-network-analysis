----------------------------------------------------
	Pre-requirements
----------------------------------------------------
You must have $JAVA_HOME environment variable set up
to build succecfully.
authentication_config.txt should be created in config/
directory. It contents should be as follows:
vk_login
vk_password

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
