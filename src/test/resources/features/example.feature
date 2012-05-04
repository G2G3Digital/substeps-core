# Copyright Technophobia Ltd 2012
Feature: Some Feature
	In order to develop a new cuke framework I need some tests
	
  Scenario: Execute simple cuke annotated methods 
		Given something
		Given series of substeps is executed 			# defined in substeps
		Given substep with param1 "wobble" and param2 "wibble"
		When an event occurs 


Scenario: A second scenario
		Then bad things happen 
		And people get upset
		Whatever yee hah
		And a parameter fred is supplied
  
  
  
#	Background: 
#    Given I'm an un-registered user
#    And I am on the registration page
      
                		