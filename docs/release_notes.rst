Substeps Core - Release Notes
=============================

- A list of changes per release 

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
