-- ============================================================================
-- Script d'initialisation de la base de données PostgreSQL
-- E-Platform Backend
-- ============================================================================

-- La base de données eplatform_db et l'utilisateur eplatform_user
-- sont déjà créés par les variables d'environnement du conteneur Docker

-- Créer le schéma public si nécessaire (normalement déjà existant)
CREATE SCHEMA IF NOT EXISTS public;

-- Donner tous les droits à l'utilisateur sur le schéma public
GRANT ALL PRIVILEGES ON SCHEMA public TO eplatform_user;

-- Autoriser la création de tables
GRANT CREATE ON SCHEMA public TO eplatform_user;

-- Configurer le search_path par défaut
ALTER ROLE eplatform_user SET search_path TO public;

-- Activer l'extension UUID (utile pour les IDs)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Activer l'extension pgcrypto (utile pour le chiffrement)
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Message de confirmation
DO $$
BEGIN
    RAISE NOTICE 'Database initialization completed successfully!';
    RAISE NOTICE 'Database: eplatform_db';
    RAISE NOTICE 'User: eplatform_user';
    RAISE NOTICE 'Schema: public';
    RAISE NOTICE 'Extensions: uuid-ossp, pgcrypto';
END $$;
