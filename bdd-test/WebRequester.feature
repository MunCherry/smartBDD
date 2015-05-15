Scenario 1: Send request to valid url
Given a web requester
When we send a request to "http://www.google.com"
Then the response contains "google"

Scenario 2: Send request to invalid url 2
Given a web requester with url as "http://www.google.com"
When we send a request
Then the response contains "google"
