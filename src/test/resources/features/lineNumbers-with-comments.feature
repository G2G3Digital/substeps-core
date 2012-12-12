# Copyright Technophobia Ltd 2012
Feature: A feature for testing line numbers of scenarios and steps in the parser when comments are present between the steps
	
#   Scenario: A commented out scenario 
#    Given I'm an un-registered user
#    And I am on the registration page

  Scenario: Execute simple cuke annotated methods 
		Given something with an@emailaddress.com
		# Theres a comment here
		Given series of substeps is executed 			# defined in substeps
		# Theres another comment here
		Given substep with param1 "wobble" and param2 "wibble"