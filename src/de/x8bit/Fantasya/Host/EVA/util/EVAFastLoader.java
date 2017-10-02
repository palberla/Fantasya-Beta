package de.x8bit.Fantasya.Host.EVA.util;

import java.sql.SQLException;

import de.x8bit.Fantasya.Atlantis.Messages.SysMsg;
import de.x8bit.Fantasya.Host.Datenbank;
import de.x8bit.Fantasya.Host.serialization.MigrationSerializerFactory;
import de.x8bit.Fantasya.Host.serialization.Serializer;
import de.x8bit.Fantasya.Host.Updates;

/**
 * Der EVAFastLoader soll das gesamte Daten- / Objektmodell aus der MySQL-
 * Datenbank lesen; genau wie es die Load...()-Routinen der einzelnen Objekte
 * tun würden. Das ganze soll schnell sein.
 * @author hb
 */
public class EVAFastLoader {

    public static void loadAll() throws SQLException {
		new SysMsg("EVAFastLoader.loadAll...");

        Updates.UpdateRegionenEntstandenIn(); // sicherstellen, dass es 'regionen.entstandenin' gibt
        Updates.UpdateMessageLongtext(); // Meldungen für extrem lange (Kampf-)Meldungen wappnen
		Updates.UpdateNegativesEinkommen(); // es kann jetzt auch ein negatives Einkommen gespeichert werden (Handel / Einkauf, Gebäudeunterhalt...)
		Updates.UpdateInselErzeugung(); // auf April2011 wechseln
        Updates.UpdateParteiPropertiesLongtext(); // 18.04.2012
        
		// fürs Debuggen (automatisiertes Testen) aus Performance-Gründen deaktiviert.
        if (!ZATMode.CurrentMode().isDebug()) Updates.UpdateUnterweltAktivieren();

        Datenbank db = new Datenbank("EVAFastLoader");

		//-------------------------------------------------------------
		// Hack: Leite das Laden uebergangsweise zum fertig implementierten Teil
		// des neuen Load/Save-Systems um.
		Serializer newStuff = MigrationSerializerFactory.buildSerializer(db);

		// zur Zeit implementiert:
		// - Parteien
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
		newStuff.loadAll();
		//--------------------------------------------------------------

		db.Close();
    }
}
