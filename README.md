# HW5
### Run
Скрипти для створення бази даних та таблиць знаходяться в src/main/resources/schema.sql та src/test/schema_test.sql. 

Для запуску треба викликати метод Hw5Application та перейти за посиланням http://localhost:8083/. 

### Links 
* http://localhost:8083/api/books - виводить всі книжки. 

> GET /api/books?offset=1&limit=1
* http://localhost:8083/api/books/create - зберігає книжку(назва книжки "name", автор "author", id жанру "genreId"). 

> POST http://localhost:8083/api/books/create
> 
> Content-Type: application/json
> 
> {
>   "name": "book",
>   "author": "author",
>   "genreId": 1
> }
* http://localhost:8083/api/books/{id} - PUT модифікує книжку по id.

> PUT http://localhost:8083/api/books/1
> 
> Content-Type: application/json
> 
> {
>   "author": "ttttt",
>   "genreId": 200
> }
* http://localhost:8083/api/books/{id} - GET дістає книжку по id. 

> GET http://localhost:8083/api/books/12
* http://localhost:8083/api/books/delete/{id} - видаляє книжку по id.

> DELETE http://localhost:8083/api/books/delete/2
* http://localhost:8083/api/books/deleteAll - видаляє всі книжки. 

> DELETE http://localhost:8083/api/books/deleteAll
* http://localhost:8083/api/books/_search - шукає книжки по одному або по двох з полів: назва книжки "name", жанр "genre". 

> POST http://localhost:8083/api/books/_search
> 
> Content-Type: application/json
> 
> {
>   "name": "999",
>   "genre": "detective"
> }
* http://localhost:8083/api/genres - виводить всі елементи сутності 2 - жанри. 

> GET http://localhost:8083/api/genres
