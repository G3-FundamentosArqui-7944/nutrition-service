-- nutrition-service initial schema

CREATE TABLE meal_records (
    id                   BIGSERIAL PRIMARY KEY,
    user_id              BIGINT NOT NULL,
    meal_type            VARCHAR(20) NOT NULL,
    description          VARCHAR(200) NOT NULL,
    calories             NUMERIC(19, 4) NOT NULL,
    protein_grams        NUMERIC(19, 4) NOT NULL,
    carbohydrates_grams  NUMERIC(19, 4) NOT NULL,
    fat_grams            NUMERIC(19, 4) NOT NULL,
    fiber_grams          NUMERIC(19, 4) NOT NULL,
    consumed_at          TIMESTAMP NOT NULL,
    source_analysis_id   BIGINT,
    created_at           TIMESTAMP NOT NULL,
    updated_at           TIMESTAMP NOT NULL
);

CREATE INDEX idx_meal_records_user_consumed ON meal_records(user_id, consumed_at DESC);

CREATE TABLE nutrition_plans (
    id                         BIGSERIAL PRIMARY KEY,
    user_id                    BIGINT NOT NULL,
    name                       VARCHAR(120) NOT NULL,
    description                VARCHAR(2000),
    daily_calories             NUMERIC(19, 4),
    daily_protein_grams        NUMERIC(19, 4),
    daily_carbohydrates_grams  NUMERIC(19, 4),
    daily_fat_grams            NUMERIC(19, 4),
    daily_fiber_grams          NUMERIC(19, 4),
    start_date                 TIMESTAMP NOT NULL,
    end_date                   TIMESTAMP,
    active                     BOOLEAN NOT NULL,
    created_at                 TIMESTAMP NOT NULL,
    updated_at                 TIMESTAMP NOT NULL
);

CREATE INDEX idx_nutrition_plans_user_active ON nutrition_plans(user_id, active);

CREATE TABLE nutrition_analyses (
    id                          BIGSERIAL PRIMARY KEY,
    user_id                     BIGINT NOT NULL,
    image_storage_key           VARCHAR(500) NOT NULL,
    image_storage_url           VARCHAR(1000) NOT NULL,
    summary                     VARCHAR(4000),
    total_calories              NUMERIC(19, 4),
    total_protein_grams         NUMERIC(19, 4),
    total_carbohydrates_grams   NUMERIC(19, 4),
    total_fat_grams             NUMERIC(19, 4),
    total_fiber_grams           NUMERIC(19, 4),
    status                      VARCHAR(20) NOT NULL,
    failure_reason              VARCHAR(500),
    ai_model_version            VARCHAR(80),
    analyzed_at                 TIMESTAMP,
    created_at                  TIMESTAMP NOT NULL,
    updated_at                  TIMESTAMP NOT NULL
);

CREATE INDEX idx_nutrition_analyses_user ON nutrition_analyses(user_id);

CREATE TABLE food_detections (
    id                     BIGSERIAL PRIMARY KEY,
    food_name              VARCHAR(120) NOT NULL,
    portion_grams          NUMERIC(7, 2) NOT NULL,
    calories               NUMERIC(19, 4) NOT NULL,
    protein_grams          NUMERIC(19, 4) NOT NULL,
    carbohydrates_grams    NUMERIC(19, 4) NOT NULL,
    fat_grams              NUMERIC(19, 4) NOT NULL,
    fiber_grams            NUMERIC(19, 4) NOT NULL,
    confidence             NUMERIC(4, 3) NOT NULL,
    nutrition_analysis_id  BIGINT REFERENCES nutrition_analyses(id) ON DELETE CASCADE,
    created_at             TIMESTAMP NOT NULL,
    updated_at             TIMESTAMP NOT NULL
);

CREATE INDEX idx_food_detections_analysis ON food_detections(nutrition_analysis_id);
