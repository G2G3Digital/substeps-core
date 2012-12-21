# Copyright Technophobia Ltd 2012
# a file containing all aspects and capabilities of this BDD runner

#### basic feature / scenario functionality

Feature: A feature to test BDD Runner functionality

Background: scenario background
    Test_Given some background

Scenario: Simple top level scenario
    Test_Given something
    Test_Then something with a "quoted parameter"
    SingleWord
      


####### OUTLINE FUNCTIONALITY    


Scenario Outline: Simple top level outline scenario
    Test_Then something with a "<param>"
    Test_Given something
Examples:
|param|
|bob|
|fred|
|barf|    
    

####### SOME SUBSTEPS       

Scenario: A feature calls some substeps
       Test_Given something defined in a substep
       Test_Then something has happened 
       

Scenario: A feature calls some more substeps
       Test_Given "something in quotes" defined in another substep
       Test_Then something else has happened 


###### Passing in a table as a parameter to a method

Scenario: a scenario is executed with a table as a parameter
    Test_Given a step is defined with a table parameter
          | param1  |  param2  |    param3  |   param4  |
          | W       |   X      |    Y       |     Z     |         

##### todo, pass the tables down to substeps, substeps use the values in the table....    


Scenario: quoted hashes are not interpreted as comments
    Test_Then something with a "#quoted parameter"
        