# Copyright Technophobia Ltd 2012
Feature: params Feature

  Scenario: pass some quoted params to a substep 
		Given something
		Given a step with a parameter "Hello bobby bobster"
		
Tags: nested_params_bug		
  Scenario: A nested parameter passing scenario 
        Given I register on Liferay 6 as "Basic" "Flow"
        