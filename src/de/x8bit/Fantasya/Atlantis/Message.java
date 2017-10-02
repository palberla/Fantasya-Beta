package de.x8bit.Fantasya.Atlantis;

import de.x8bit.Fantasya.Atlantis.Helper.MessageCache;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

import de.x8bit.Fantasya.Atlantis.Messages.BigError;
import de.x8bit.Fantasya.util.comparator.MeldungsKategorieComparator;
import de.x8bit.Fantasya.util.PackageLister;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * das neue Message-System ... es werden für jeden Bereich eine entsprechende
 * Klasse angelegt ... die Klasse kümmert sich dann um die Zuweisung zur DB & so
 * 
 * Beachte: Messages werden anhand ihrer EvaId eindeutig identifiziert. Diese Id
 * wird zum Vergleichen mittels equals() und zum Sortieren verwendet, und sollte
 * daher fuer einen gegebenen ZAT eindeutig sein.
 * 
 * @author  mogel
 */
public class Message {
    private static SortedSet<Class<? extends Message>> Arten;
	protected static Map<String, Class<? extends Message>> ART_KEY;

	private Partei partei = null;
	private Unit unit = null;
	private String text = "";
	private Coords coords = null;
    private Date timestamp = null;
	private int evaId = -1;


    private static boolean Mute = false;

	private final static MessageCache Messages = new MessageCache();


	private static int AUTO_INCREMENT = 1;

	
	public Message() {
        this.timestamp = new Date();
		this.evaId = AUTO_INCREMENT++;
    }

	
	@SuppressWarnings("unchecked")
    public static Set<Class<? extends Message>> AlleArten() {
        if (Arten == null) {
            Arten = new TreeSet<Class<? extends Message>>(new MeldungsKategorieComparator());
			ART_KEY = new HashMap<String, Class<? extends Message>>();
            try {
                List<Class> klassen = PackageLister.getClasses("de.x8bit.Fantasya.Atlantis.Messages");
                for (Class clazz : klassen) {
                    Arten.add(clazz);
					ART_KEY.put(clazz.getSimpleName(), clazz);
					ART_KEY.put(clazz.getSimpleName().toLowerCase(), clazz);
					ART_KEY.put(clazz.getSimpleName().toUpperCase(), clazz);

                }
                Arten = Collections.unmodifiableSortedSet(Arten);
				ART_KEY = Collections.unmodifiableMap(ART_KEY);

            } catch (ClassNotFoundException ex) {
                new BigError(ex);
            } catch (IOException ex) {
                new BigError(ex);
            } catch (URISyntaxException ex) {
                new BigError(ex);
            }
        }

        return Arten;
    }

    /**
     * schaltet alle Messages (auch abgeleitete Klassen) "stumm" - es werden 
     * weder Ausgaben nach stdio gemacht noch Objekte in Message.messages 
     * abgelegt.
     */
    public static void Mute() {
        Message.Mute = true;
    }

    /**
     * schaltet die Behandlung aller Messages auf normal - es werden Ausgaben
     * auf stdio gemacht und neue Message-Objekte werden in Message.messages
     * abgelegt.
     */
    public static void Unmute() {
        Message.Mute = false;
    }

    /**
     * @return true, wenn Message.Mute() aktiv ist - @see Mute() @see Unmute()
     */
    public static boolean IsMute() {
        return Message.Mute;
    }

	/**
	 * löscht alle Meldungen aus der Datenbank
	 */
	public static void DeleteAll()
	{
		System.out.println("Lösche alle Meldungen.");
		Messages.clear();
	}

	/** Liefert Messages ohne Beruecksichtigung der Kategorie zurueck. */
    public static List<Message> Retrieve(Partei p, Coords c, Unit u) {
		// Kategorie ignorieren
		return Retrieve(p, c, u, null);
    }

	public static List<Message> Retrieve(Partei p, Coords c, Unit u, String kategorie) {		
		Collection<Message> candidateMessages = Messages;
		
		if (p != null && c != null) {
			candidateMessages = Messages.getAll(c, p.getNummer());
		} else if (p != null) {
			candidateMessages = Messages.getAll(p.getNummer());
		} else if (c != null) {
			candidateMessages = Messages.getAll(c);
		}
		
		List<Message> messages = new ArrayList<Message>();
		for (Message msg : candidateMessages) {
			if (u != null && msg.getUnit() != u) {
				continue;
			}
			if (kategorie != null && !msg.getClass().getSimpleName().equals(kategorie)) {
				continue;
			}
			messages.add(msg);
		}
		
		return messages;
    }
	
	/** letzte Meldung merken - für Fehler SMS */
	protected static String lastMessage;
 
	
	/** Initializes a new message and prints it to stdout.
	 * 
	 * @param level The debug level of the message. If it is above the threshold, the message is not printed.
	 * @param msg The message text.
	 * @param partei The player who the message is directed to.
	 * @param coords The coordinates of the region that the message refers to.
	 * @param unit The unit that the message refers to.
	 */
	protected void print(int level, String msg, Partei partei, Coords coords, Unit unit) {
		// first, some sanity checks.
		if (msg == null || msg.trim().isEmpty()) {
			throw new IllegalArgumentException("Messages must have a text!");
		}

		// print the message to stdout
		if (!Mute) {
			String kategorie = "[" + this.getClass().getSimpleName() + "] ";
		    if (kategorie.equals("[Battle] ")) kategorie = "";
			System.out.println(kategorie + msg);
		}
		
		this.text = msg;
		this.partei = partei;
		this.coords = coords;
		this.unit = unit;
        Messages.add(this);
	}
	
	/** Convenience function if only the party is set. */
	protected void print(int level, String msg, Partei partei) {
		this.print(level, msg, partei, null, null);
	}
	
	/** Convenience function if only the party and the coordinates are set. */
	protected void print(int level, String msg, Partei partei, Coords coords) {
		this.print(level, msg, partei, coords, null);
	}

	/** Convenience funtion if only the unit and the coordinates are set.
	 * 
	 * The party is taken from the unit's owner. The unit must not be null.
	 */
	protected void print(int level, String msg, Coords coords, Unit u) {
		this.print(level, msg, Partei.getPartei(u.getOwner()), coords, u);
	}
	
	@Override
	public int hashCode() {
		int hash = 3;
		hash = 97 * hash + this.evaId;
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		final Message other = (Message) obj;
		if (this.evaId != other.evaId) {
			return false;
		}
		return true;
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName()).append(" ");
		sb.append("id[").append(evaId).append("]");
		if (getPartei() != null) sb.append("P[").append(getPartei().getNummerBase36()).append("] ");
		if (getUnit() != null) sb.append("U[").append(getUnit().getNummerBase36()).append("] ");
		if (getCoords() != null) sb.append(getCoords()).append(" ");
		int cut = 40;
		if (getText().length() > cut) {
			sb.append("'").append(getText().substring(0, cut-1)).append("'");
		}
		
		return sb.toString();
	}



	public static int TotalCount() {
		return Messages.size();
	}

	
	public void setPartei(Partei partei) {
		this.partei = partei;
	}
	public Partei getPartei() {
		return partei;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}
	public Unit getUnit() {
		return unit;
	}

	public void setText(String message) {
		this.text = message;
	}
	public String getText() {
		return text;
	}

	public void setCoords(Coords coords) {
		this.coords = coords;
	}
	public Coords getCoords() {
		return coords;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public int getEvaId() {
		return evaId;
	}

	public static MessageCache Cache() {
		return Messages;
	}


	public Date getTimeStamp() {
		return this.timestamp;
	}
}
