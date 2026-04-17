-- public.coin definition

-- Drop table

-- DROP TABLE coin;

CREATE TABLE coin (
	id bigserial NOT NULL,
	symbol varchar(10) NULL,
	created_dt timestamp NULL,
	created_by varchar(255) NULL,
	updated_dt timestamp NULL,
	updated_by varchar(255) NULL,
	pricing varchar(255) NULL,
	CONSTRAINT coin_pk PRIMARY KEY (id),
	CONSTRAINT coin_unique_symbol UNIQUE (symbol)
);


-- public.coin_error definition

-- Drop table

-- DROP TABLE coin_error;

CREATE TABLE coin_error (
	id bigserial NOT NULL,
	batch text NULL,
	error_msg text NULL,
	created_dt timestamp DEFAULT now() NULL,
	raw_data text NULL,
	CONSTRAINT coin_error_pk PRIMARY KEY (id)
);