1. 사용 기술 스택.
    - back
        - JRE : 22
        - Spring Boot : 3.3
            - Tomcat Port : 8080
        - Python : 3.10
    - Database
        - NoSQL
            - Redis : 3.1 ver
                - Port : 6379
                - db 0 : Refresh Token Access Token ( String / String )
                    - key : Access Token / value : Refresh Token
                - db 1 : jwt black list ( Long / String )
                    - key : index / value : Token ID
                - db 2 : visit log ( Long / Json )
                    - key : member_id / value : json ( date : visit url )
        - RDBMS
            - PostgreSQL (Live) : 16.4 ver
                - Port : 5432
            - H2 Database (Test)
    - Front
        - Thymeleaf (html5)
        - ChatGPT-4o