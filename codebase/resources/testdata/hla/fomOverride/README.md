FOM Overriding Tests
======================
When configuring Disco, a user has the option of specifying a FOM overrides folder, that if used
defines a folder from which FOM modules should be loaded instead of using the default RPR modules
that are shipped within the JAR.

This folder is used to support tests, and it is important that only the contained modules that are
present are kept, as the tests will confirm the number of expected modules based on the module
count (so adding or removing will break the tests) and on the specific file names.
