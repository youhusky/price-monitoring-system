# Price Monitoring/Notification System
Design a price monitoring system to monitor product price and notify subscribers if the price is reduced. Similar to Dealmoon.com.

## Features
- Keeps track of product prices at an e-commerce web site.
- Allows users to subscribe by emails for interested product categories.
- Notifies subscribers if there is any discount product in the subscribed categories.
- Subscribers can specify minimum discount threshold of each category for receiving notifications.
- Also allows users to query discount products online.

## Development environment
- **MySQL DB** is used to store categories, product list urls, products and users information.
- **Mongo DB** is used to store product crawling logs.
- **Lombok** is used to eliminate constructors and getter/setter implementation for cleaner coding style.
- **RabbitMQ** is used for communications between Product Crawler, Price Monitoring Service, Instant Notification Service and Product Log Service.
- **Redis** is used to cache last product price for fast price change comparison.
- **Jsoup** is used to retrieve web page content, parse the required text from the page

## Design Diagrams
![Overview](/Overview.png)
![MySQL Schema](/MySQLSchema.png)
![MongoDB Schema](/MongoDBSchema.png)
![Category Crawler](/CategoryCrawler.png)
![Product Crawler](/ProductCrawler.png)
![Price Monitoring Service](/PriceMonitoringService.png)
![Instant Notification Service](/InstantNotificationService.png)
![Product Log Service](/ProductLogService.png)
![User Service](/UserService.png)
