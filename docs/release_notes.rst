Substeps Core - Release Notes
=============================

- A list of changes per release 

1.1.1
-----
- reduced some logging from debug to trace

1.1.0
-----
- An API has been extracted from core, with a BOM project introduced to make using substeps easier
- Ant / Junit runners have been removed from core and put in modules of a new repository which also contains the Maven plugin
- Major refactor of execution node hierarchy
- Change to SusbtepsRunner interface
- Change in the way the number of scenario steps are calculated, these are now counted for each example in an outline
- Added screenshots of failures to the feature report
- BUGFIX: tailing comments not stripped from substep definition files

1.0.2
-----
- Line numbers, source offsets included in steps
- Applied standard formatter (can be found in the root of the 'Substeps' project)  
- Updated report to include step timing information
- BUGFIX: Fixed trailing commas in  detail_data.json and renamed to detail_data.js
- BUGFIX: when parsing substeps directives in SubStepDefinitionParser, catch PatternSyntaxExceptions log, rather than failing the whole syntax build
- Performance improvements for locating the first line

1.0.1
-----
- Bug in substeps.js used for the reports.

1.0.0
-----
- Further improvements around the reporting, better layout, tables of failed tags, layout etc, tag data now externalised.

0.0.8
-----
- Empty Substep definitions are now excluded and no longer throw an NPE.
- Addition of Ant task for running SubSteps
- Scenarios without steps defined no longer throw an NPE.
- Major improvements to the HTML test report (still more to do here).
- empty table values are now substituted correctly without throwing an NPE.
- bugs around new lines in report json

0.0.7
-----
- BUGFix: Scenarios that should be allowed to fail the build were still failing the build.
- Introduced a BuildFailureManager to assist with critical or otherwise failures.
- Non Critical scenarios no longer need to have the features also tagged.
- quoted #'s are now treated as values rather than comment delimitter
- Wiring in of initialisation classes via StepImplementations annotation


 
0.0.6
-----
- Changes to Notifications, single distributor with a set of listeners.
- Changes to support the eclipse plugin.
- DefaultExecutionReportBuilder will now delete the report directory if it's already there.
- substeps filename now reported in debug
- changes to support glossary building
- SyntaxBuilder now allows a ClassAnalyser to be passed in, rather than instantiated (used in eclipse plugin)
- added missing images from the basic report
