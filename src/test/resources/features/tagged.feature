# Copyright Technophobia Ltd 2012
Feature: A tagged Feature
    In order to ensure tags are working here is a test

Tags: @runme    
Scenario: A tagged scenario
    Given a substep
    
Tags: @donttunme
    Scenario: An excluded tagged scenario
    Given another substep
    
Scenario: An untagged scenario
    Given yet another substep
    
Tags:
@all
@searchcontracts
@searchcontracts_30
Scenario: multilined tagged scenario
        Given another substep
