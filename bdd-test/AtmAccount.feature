Scenario 1: Deposit failure
Given an Atm account with balance "100"
When a customer deposits "20"
Then the balance of the account should be "110"

Scenario 2: Withdraw success
Given an Atm account with balance "100"
When a customer withdraws "20"
Then the new balance of the account should be "80"

Scenario 3: Withdraw failure
Given an Atm account with balance "100"
When a customer withdraws "20"
Then the new balance of the account should be "90"
And the new balance of the account should be "250"
