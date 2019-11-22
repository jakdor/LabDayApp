  # Lab Day App - project goal
  Guide Android App for Lab Day PWR Event. Application provides event map, timetable and other informations.

  Current status: release 2.1.0
  https://play.google.com/store/apps/details?id=com.jakdor.labday

  v1 (2018) Backend repository
  https://github.com/JanStoltman/LabDayBackend

  v2 Backend repository
  https://github.com/asi-pwr/LabDayBackendSpring
  
  v3 Backend repository
  https://github.com/jakdor/LabDayBackend

  ## Technology goals
  Use as many professionally used frameworks, libraries and project patterns as saintly possible.

  MVVM with repository project architecture. Dependency Injection project pattern with Dagger2.

  Proper test coverage with all types of tests(unit, integration, GUI unit tests, automated integration GUI tests) + documentation.

  Views designed fallowing Material Design and UX guidelines, use of new Constraint & classic layouts, Data binding / Butter Knife where suitable.

  Backend providing REST API handled by Retrofit with local off-line backup in native sqlite database accessed with SQLBrite, all managed by RXJava and LiveData (no ORM this time to see performance difference/learn new things). API access tokens stored after encryption with Facebook Conceal lib.

  Google APIs used: Maps, Directions

  Some classes implemented in Kotlin, to practice new language.

  ## Technology stack

  ### Android
  Name |  Version |
  | :--: | :---: |
  | API level | 21-29 |
  | Build tools | 29.0.2 |
  | AndroidX | 1.1.0 |
  | Java | 1.8 |
  | Kotlin | 1.3 |

  ### Libs / frameworks
  Name |  Version |
  | :--: | :---: |
  | Retrofit2 | 2.3.0 |
  | OkHttp | 3.9.1 |
  | Dagger2 | 2.21 |
  | RxJava2 | 2.1.7 |
  | SQLBrite | 3.1.0 |
  | Conceal | 2.0.1 |
  | Glide | 4.9.0 |
  | GMSmaps | 17.0.0 |
  | Timber | 4.7.1 |
  | Crashlytics | 2.10.1 |
  | ... | |

  ### Testing
  Name |  Version |
  | :--: | :---: |
  | Junit4 | 4.12 |
  | Mockito | 2.13.0 |
  | Espresso | 3.0.1 |
  | LeakCanary | 1.6.3 |
  | RESTMock | 0.2.2 |
