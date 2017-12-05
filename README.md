  # Lab Day App - project goal
  Guide Android App for Lab Day PWR Event. Application will provide event map, timetable and other informations.

  ## Technology goals
  Use as many professionally used frameworks, libraries and project patterns as saintly possible.

  Proper test coverage with all types of tests(unit, integration, GUI unit tests, automated integration GUI tests). + documentation

  Views designed fallowing Material Design and UX guidelines, use of new Constraint & classic layouts, Data binding / Butter Knife where suitable.

  Backend providing REST API handled by Retrofit with local off-line backup in native sqlite database accessed with StorIO both handled by RXJava (no ORM this time to see performance difference). Additionally some mechanism to handle situation when backend is down with no local data backup.

  Implement Dependency Injection project pattern with Dagger and Reactive programming with RXJava2. 
  Some classes implemented in Kotlin, to learn new Language.

  ## Technology stack

  ### Android
  Name |  Version |
  | :--: | :---: |
  | API level | 21+ |
  | Build tools | 26.0.3 |
  | Support libs | 26.1.0 |
  | Java | 1.8 |
  | Kotlin | 1.2 |

  ### Libs / frameworks
  Name |  Version |
  | :--: | :---: |
  | Retrofit2 | 2.3.0 |
  | OkHttp | 3.9.1 |
  | Glide | 4.3.1 |
  | ButterKnife | 8.8.1 |
  | ... | |

  ### Testing
  Name |  Version |
  | :--: | :---: |
  | Junit4 | 4.12 |
  | Espresso | 3.0.1 |
  | Roboelectric | 3.5.1 |
