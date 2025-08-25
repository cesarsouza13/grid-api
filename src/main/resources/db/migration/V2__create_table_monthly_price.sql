CREATE SEQUENCE monthly_price_seq;
CREATE TABLE monthly_price (
    id BIGINT PRIMARY KEY DEFAULT nextval('monthly_price_seq'),
    date DATE NOT NULL,
    region VARCHAR(20) NOT NULL,
    price NUMERIC(10,2) NOT NULL,
    CONSTRAINT chk_region CHECK (region IN ('SUDESTE','SUL','NORTE','NORDESTE'))
);