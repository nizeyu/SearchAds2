## ReadMe
### Author: Zeyu Ni
### Program Name: Index Builder Test

----------
Index Builder Test is a program to test index builder function in AdsSearch Project

### Steps to run the program:
(1) Download project file

    git clone https://github.com/nizeyu/SearchAds.git


(2)   Create Java Project: AdsPulisher in Eclipse or IntelliJ IDEA

(3)   Create Java Project: IndexBuilder in Eclipse or IntelliJ IDEA

(4)   Create two tables in Database "Searchads"

    ### Table: campaign
    Columns:
    campaignId  int(11) PK
    budget      double

	### Table: ad
	Columns:
	adId        int(11) PK
	campaignId  int(11)
	keyWords    varchar(1024)
	bidPrice    double
	price       double
	thumbnail   mediumtext
	description mediumtext
	brand       varchar(1024)
	detail_url  mediumtext
	category    varchar(1024)
	title       varchar(2048)

(5)   Start Memcached： In terminal, type in /usr/local/bin/memcached -d -p 11211

(6)   run AdsPublisher.java in AdsPublisher to publish ads information to RabbitMQ

(7)   run Test.java to consume from RabbitMQ to build invert index and store in MySQL

Finished! Thank for review!
