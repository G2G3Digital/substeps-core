# Copyright Technophobia Ltd 2012
Feature: Some Feature2
	In order to develop a new cuke framework I need some tests

#   Scenario: A commented out scenario 
#    Given I'm an un-registered user
#    And I am on the registration page

Background: background to the proceeding scenario
    Given whatever

Tags: @tag1 @tag2	
  Scenario: Execute simple cuke annotated methods 
		Given something with an@emailaddress.com
		Given series of substeps is executed 			# defined in substeps
		Given substep with param1 "wobble" and param2 "wibble"
		When an event occurs 

Tags: @tag3
Scenario: A second scenario
		Then bad things happen 
		And people get upset
		Whatever yee hah
		And a parameter fred is supplied
  
      
Scenario Outline: User submitting blank registration form
        When I register as a new user 
      And I enter a <Object> of <Data>
          And I click Submit
        Then I am <Result>
            And I am not registered with the system
            And I am shown the registration page  
  
  Examples:
    |Object|Data|Result|
    |Title||Please enter a title.|
    |First name||Please enter a valid first name.|
    |Last name||Please enter a valid last name.|
    |Email||Please enter a valid email address|
    |Professional title||Please enter your professional title|
    |Password||That password is too short (or too long). Please make sure your password is a minimum of 8 characters.|
    |Retype Password||no error message|
    |Password reminder||Your password reminder cannot be blank or contain your password |        
    
    
Scenario: inline table
    And I click the 'Create user' navigation link
    Then I see the 'Create user' page with heading 'Create User step 1 of 5'
    And I type the following into the 'Enter new user details' fields
        |New user email address|New user forename |New user surname |New user telephone number|
        |table1.newEmail       |table1.newForename|table1.newSurname|table1.newTel            |
    And I click the 'Next' button
    And whatever            		