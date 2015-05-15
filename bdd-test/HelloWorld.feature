Scenario 1: Success 
Given HelloWorld
When value is "Hello World"
Then compare with "Hello World"

Scenario 2: Failure
Given HelloWorld with "Hello", "0"  
When value is "Not a Hello World"
Then compare with "Hello World"
