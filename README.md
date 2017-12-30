  # Lab Day App - project goal
  Guide Android App for Lab Day PWR Event. Application will provide event map, timetable and other informations.

  Current status: indev

  ## Technology goals
  Use as many professionally used frameworks, libraries and project patterns as saintly possible.

  MVVM with repository project architecture.

  Proper test coverage with all types of tests(unit, integration, GUI unit tests, automated integration GUI tests) + documentation.

  Views designed fallowing Material Design and UX guidelines, use of new Constraint & classic layouts, Data binding / Butter Knife where suitable.

  Backend providing REST API handled by Retrofit with local off-line backup in native sqlite database accessed with StorIO/SQLBrite, all managed by RXJava and LiveData (no ORM this time to see performance difference/learn new things). API access tokens stored after encrytion with Facebook Conceal lib. Additionally some mechanism to handle situation when backend is down with no local data backup.

  Dependency Injection project pattern with Dagger2 and Reactive programming with RXJava2/LiveData. 
  Some classes implemented in Kotlin, to learn new Language.

  ## Technology stack

  ### Android
  Name |  Version |
  | :--: | :---: |
  | API level | 21-27 |
  | Build tools | 27.0.2 |
  | Support libs | 27.0.2 |
  | Java | 1.8 |
  | Kotlin | 1.2 |

  ### Libs / frameworks
  Name |  Version |
  | :--: | :---: |
  | Retrofit2 | 2.3.0 |
  | OkHttp | 3.9.1 |
  | Dagger2 | 2.13 |
  | RxJava | 2.1.7 |
  | Conceal | 2.0.1 |
  | Glide | 4.4.0 |
  | ButterKnife | 8.8.1 |
  | ... | |

  ### Testing
  Name |  Version |
  | :--: | :---: |
  | Junit4 | 4.12 |
  | Mockito | 2.13.0 |
  | Espresso | 3.0.1 |
  | Roboelectric | 3.5.1 |
  | DaggerMock | 0.8.2 |
  | RESTMock | 0.2.2 |
