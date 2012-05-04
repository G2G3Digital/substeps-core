# Copyright Technophobia Ltd 2012
# a feature to test that we're calling before and after setup methods correctly and the correct number of times

Tags: @beforesAndAfters

Feature: A feature to test Before And after functionality 1

Scenario: before and after scenario 1
    Test_Given something
    Test_Then something blows up
    
Scenario: before and after scenario 2
    Test_Given something
    Test_Then something with a "quoted parameter"    