# Copyright Technophobia Ltd 2012
###### Passing in a table as a parameter to a method

Feature: Inline tables (replicated for debugging)

Scenario: a scenario is executed with a table as a parameter
    Test_Given a step is defined with a table parameter
          | param1  |  param2  |    param3  |   param4  |
          | W       |   X      |    Y       |     Z     |       