# Copyright Technophobia Ltd 2012
Feature: A feature with an invalid spec 

  Tags:
  @invalid_scenario_outline
  Scenario Outline: An outline with null parameter passing
  	Given substep with param1 <name1> and param2 <name2>

#  And I see '<contract_subtype>'

# can't pass null from an example into a substep - doesn't really make sense
# And I see 'null' ?? And I see '' ??
  
  Examples:
  |name1|name2|
  #|value1|value2|
  |||
