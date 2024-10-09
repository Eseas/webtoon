## 프로젝트 노션
[https://www.notion.so/10ac907c2a5b8072bd1ad4750d31cd0c](https://secret-letter-54e.notion.site/10ac907c2a5b8072bd1ad4750d31cd0c?pvs=4)

## 사용 기술 스택.
    - back
        - JRE : 22
        - Spring Boot : 3.3
            - Tomcat
                - Https Port : 8443
                - Http Port : 8080
                - https ssl 인증서 발급 : openssl ( 사설 인증서 )
        - Python : 3.10
    - Database
        - NoSQL
            - Redis : 3.1 ver
                - Port : 6379
                - db 0 : Access Token Refresh Token ( String / String )
                    - key : Access Token / value : Refresh Token
                - db 1 : jwt black list ( Long / String )
                    - key : index / value : Token ID
                - db 2 : visit log ( Long / Json )
                    - key : member_id / value : json ( date : visit url )
                - db 3 :
        - RDBMS
            - PostgreSQL (Live) : 16.4 ver
                - Port : 5432
            - H2 Database (Test)
    - Front
        - Thymeleaf (html5)
        - ChatGPT-4o
