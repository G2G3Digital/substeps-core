# Copyright Technophobia Ltd 2012
Feature: A feature to illustrate bugs found

Tags: @bug1
Scenario: Failures in child sub steps doesn't result in stoppage
    Given step 1
    Given step 2
    
Tags: @table_params_and_outline
Scenario Outline: an outline scenario with table parameters
   Given a step with a table argument
      | column1 | column2 |
      | <val1> | <val2> |
Examples:
  |val1 |val2 |
  |one |two |

# <ltd_company_button> is not substituted for Yes

Tags: @substitution_problem
Scenario Outline: a scenario with a substitution issue
	Then a method with a quoted '<message>'
	
  Examples:
| message                                                                                                 |
| You must enter the following information to proceed:$Sort code.$Bank Account Name.$Bank Account Number.|
	    