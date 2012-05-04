# Copyright Technophobia Ltd 2012
# future - nice to have
     
Feature: a way of running a background outline a number of times with different data, effectively a way of nesting outlines, and passing the data through.. 

Background Outline: scenario outline background
    Given some outline background
Examples:
|X|
|1|
|2|    

Scenario Outline: Simple top level outline scenario
    Given something
    Then something with a "<param>"
Examples:
|param|
|bob|
|fred|    