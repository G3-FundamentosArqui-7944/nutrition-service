-- "Registrar como comida" prefills the meal description with the AI analysis summary
-- (up to 4000 chars, see nutrition_analyses.summary), which overflowed the original
-- 200-char limit and made every log-meal call fail with:
--   "value too long for type character varying(200)"
ALTER TABLE meal_records ALTER COLUMN description TYPE VARCHAR(500);
