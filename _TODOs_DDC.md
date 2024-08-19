# IDEAS

.. to tranform into issues ?

- [IN PROGRESS] check `pom.xml` dependencies (usefulness, scope, versions)
    - [DONE] first clean, check scopes, factorization
    - check usefulness
    - upgrade versions
- enable github `dependabot` to create automatically PR with version upgrades of dependencides (when all dependencies will be ok)
- ménage dans les branches de dev (local et remote)
- docker-compose : ":9000" (génération port aléatoire pour l'IHM + repérage pour IHM) au lieu de "9000:9000" si erreur lors du démarrage "Error response from daemon: Ports are not available: exposing port TCP 0.0.0.0:9000 -> 0.0.0.0:0: listen tcp 0.0.0.0:9000: bind: address already in use"