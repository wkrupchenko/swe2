SELECT b
FROM Bestellung b
JOIN b.bKunde k
WHERE k.kNachname LIKE 'G%'

SELECT a
FROM Artikel a
JOIN a.aArtikelgruppe g
WHERE g.agName = 'Sommermode'

SELECT b
FROM Bestellung b
JOIN b.bLieferungen l
WHERE l.lLiefernr LIKE '%-003' OR  l.lLiefernr LIKE '%-004'