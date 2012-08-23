Here we pack 2 class into this jar file
one is the main class that WebScrapper is main entrance for our program. It will initiate the search and do all the operation
Another class Result is the class use to store the search information. it will create and access by 1st one.

Note on doing search :
Handle the two queries above:
    Query 1: (requires a single argument)
    java -jar Assignment.jar <keyword> (e.g. java -jar Assignment.jar "baby strollers")
    Query 2: (requires two arguments)
    java -jar Assignment.jar <keyword> <page number> (e.g. java -jar Assignment.jar "baby strollers" 2)

    this program receive one or two argument input, note that words in double quote("") is count as single argument
    and this is suggested format for search keyword