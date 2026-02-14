CREATE SCHEMA IF NOT EXISTS portfolio;

INSERT INTO portfolio.portfolio (user_id, name)
    VALUES
        (101, 'My first portfolio'),
        (102, 'ETF Portfolio'),
        (103, 'Index Funds');

INSERT INTO portfolio.owned_stock (portfolio_id, ticker, quantity, purchase_price)
    VALUES
        (101, 'MSFT', 10, 399.99),
        (101, 'EBAY', 25, 90.25),
        (101, 'AAPL', 20, 287.00),
        (102, 'DRUM', 10, 101.05),
        (102, 'HOOD', 10, 144.75),
        (103, 'CHASE', 10, 93.67);

INSERT INTO portfolio.watchlist (portfolio_id, ticker)
    VALUES
        (101, 'QQQ'),
        (101, 'VOO'),
        (101, 'VXUS'),
        (101, 'ARKK'),
        (101, 'ORCL'),
        (102, 'NOW'),
        (102, 'NVDA'),
        (103, 'AMD'),
        (103, 'TSLA'),
        (103, 'AMZN'),
        (103, 'VOOG'),
