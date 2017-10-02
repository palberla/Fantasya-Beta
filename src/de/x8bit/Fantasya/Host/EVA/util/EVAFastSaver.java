package de.x8bit.Fantasya.Host.EVA.util;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import de.x8bit.Fantasya.Atlantis.Allianz;
import de.x8bit.Fantasya.Atlantis.Allianz.AllianzOption;
import de.x8bit.Fantasya.Atlantis.Building;
import de.x8bit.Fantasya.Atlantis.Coords;
import de.x8bit.Fantasya.Atlantis.Effect;
import de.x8bit.Fantasya.Atlantis.Item;
import de.x8bit.Fantasya.Atlantis.Partei;
import de.x8bit.Fantasya.Atlantis.Region;
import de.x8bit.Fantasya.Atlantis.Richtung;
import de.x8bit.Fantasya.Atlantis.Ship;
import de.x8bit.Fantasya.Atlantis.Skill;
import de.x8bit.Fantasya.Atlantis.Spell;
import de.x8bit.Fantasya.Atlantis.Unit;
import de.x8bit.Fantasya.Atlantis.Helper.Nachfrage;
import de.x8bit.Fantasya.Atlantis.Items.Holz;
import de.x8bit.Fantasya.Atlantis.Items.Seide;
import de.x8bit.Fantasya.Atlantis.Messages.BigError;
import de.x8bit.Fantasya.Atlantis.Messages.SysErr;
import de.x8bit.Fantasya.Atlantis.Messages.SysMsg;
import de.x8bit.Fantasya.Atlantis.Regions.Ebene;
import de.x8bit.Fantasya.Atlantis.Skills.Wahrnehmung;
import de.x8bit.Fantasya.Atlantis.Spells.HainDerTausendEichen;
import de.x8bit.Fantasya.Atlantis.Steuer;
import de.x8bit.Fantasya.Host.Datenbank;
import de.x8bit.Fantasya.Host.serialization.MigrationSerializerFactory;
import de.x8bit.Fantasya.Host.serialization.Serializer;
import java.util.HashSet;
import java.util.Set;

/**
 * Absicht dieser Klasse ist nicht weniger, als das gesamte Daten-Objektmodell
 * vollständig und SCHNELL in die MySQL-Datenbank zu schreiben. Der EVAFastSaver
 * soll dabei die Datenbank-Routinen der einzelnen Objekte ersetzen - sie sollen
 * eher nicht entfernt werden, aber funktionsidentisch gedoppelt.
 * @author hapebe
 */
@SuppressWarnings("unused") // hier wird der result des DB-Query unterdrückt
public class EVAFastSaver {

	/**
	 * Anzahl der Objekte, die in EINEM SQL-Statement gemeinsam in die DB geschrieben werden
	 */
	public static final int ROWS_PER_INSERT = 50;
	/**
	 * Intervall (Anzahl) der Meldungen über geschriebene Objekte
	 */
	public static final int LOG_INTV = 1000;

	public static void saveAll(boolean clear) throws SQLException {
		new SysMsg("EVAFastSaver.saveAll...");

		Datenbank db = new Datenbank("EVAFastSaver");

		//-------------------------------------------------------------
		// Hack: Leite das Speichern uebergangsweise zum fertig implementierten Teil
		// des neuen Load/Save-Systems um.
		Serializer newStuff = MigrationSerializerFactory.buildSerializer(db);

		// zur Zeit implementiert:
		// - Parteien.
		// - Allianzen
		// - Steuern
		// - Parteienproperties
		// - Regionen
		// - Resourcen
		// - Strassen
		// - Luxus
		// - Regionenproperties
		// - Gebaeude + Properties
		// - Schiffe
		// - New Player
		// - Befehle
		// - Messages
		newStuff.saveAll();
		//-------------------------------------------------------------


		db.Close();
	}
}
