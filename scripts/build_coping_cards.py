# build_coping_cards.py — 864zeros Content Pipeline for ClearStreak
import sqlite3
import csv
import uuid
import os

DB_NAME = "coping_cards.db"

def init_db(db_path=DB_NAME):
    conn = sqlite3.connect(db_path)
    c = conn.cursor()
    c.execute('''
        CREATE TABLE IF NOT EXISTS coping_cards (
            id TEXT PRIMARY KEY,
            trigger_category TEXT NOT NULL,
            min_urge_level TEXT NOT NULL,
            action_text TEXT NOT NULL,
            rationale TEXT,
            is_favorite INTEGER DEFAULT 0,
            is_user_created INTEGER DEFAULT 0
        )
    ''')
    conn.commit()
    return conn

def ingest_from_csv(conn, csv_path):
    if not os.path.exists(csv_path):
        print(f"CSV file {csv_path} not found.")
        return
    with open(csv_path, 'r', encoding='utf-8') as f:
        reader = csv.DictReader(f)
        c = conn.cursor()
        count = 0
        for row in reader:
            c.execute('''
                INSERT OR REPLACE INTO coping_cards (id, trigger_category, min_urge_level, action_text, rationale)
                VALUES (?, ?, ?, ?, ?)
            ''', (str(uuid.uuid4()), row['trigger_category'], row['min_urge_level'],
                  row['action_text'], row.get('rationale', '')))
            count += 1
    conn.commit()
    print(f"Successfully ingested {count} cards into {DB_NAME}.")

if __name__ == "__main__":
    conn = init_db()
    csv_file = os.path.join("content", "coping_cards_v1.csv")
    if os.path.exists(csv_file):
        ingest_from_csv(conn, csv_file)
    conn.close()
