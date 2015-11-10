Feature: a feature for testing out an outline


# TODO - not working here
  Tags: scenario-with-params-fail
Scenario: Direct scenario
  Given a substep that takes one parameter "no sub"
  And a substep that takes one parameter "sub"


Tags: outline-scenario-with-params-fail

Scenario Outline: scenario with table working
   #Given a substep that takes one parameter "<value2>"
   And a substep that takes one parameter "<value1>"

  Examples:
    |value1 | value2 |
    |table sub    | table no sub   |

Tags: outline-scenario-with-params-pass

Scenario Outline: scenario with table failing
     Given a substep that takes one parameter "<value2>"
    #And a substep that takes one parameter "<value1>"

    Examples:
      |value1 | value2 |
      |table sub    | table no sub   |


#  And another substep that takes the other parameter "direct"
#  Given another substep that takes the other parameter "<value2>"
  # this is the one that fails

#  Examples:
#    |value1 | value2 |
#    |bob    | fred   |