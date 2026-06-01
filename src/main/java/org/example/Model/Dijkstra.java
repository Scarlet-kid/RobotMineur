package org.example.Model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

public class Dijkstra {

    public static List<Directions> calculerChemin(Secteur[][] grille, Secteur depart, Secteur arrivee) {

        int lignes = grille.length; // nb ligne
        int cols   = grille[0].length; // nb colonne.

        int[][] distance = new int[lignes][cols]; // matrice virtuelle du monde

        Directions[][] directionPrise = new Directions[lignes][cols]; // la reconstitution du chemin du depart a l'arrivé

        for (int[] row : distance) {
            Arrays.fill(row, Integer.MAX_VALUE); // on remplace tous les sommets avec l'infini car on les a pas encore parcouru.
        }

        int dLigne   = depart.getLigne();
        int dColonne = depart.getColonne();
        int aLigne   = arrivee.getLigne();
        int aColonne = arrivee.getColonne();

        distance[dLigne][dColonne] = 0; // sommet de départ. distance=0.

        Queue<int[]> file = new ArrayDeque<>(); //file
        file.add(new int[]{dLigne, dColonne}); //on ajoute dans la file la ligne de et la colonne de départ.

        // Les 4 directions et leurs deltas
        int[][] deltas = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}};
        Directions[] directionsMap = {Directions.NORD, Directions.SUD, Directions.EST, Directions.OUEST};

        while (!file.isEmpty()) {
            int[] actuel = file.poll(); // lit l'élément et le supprime.
            int l = actuel[0];
            int c = actuel[1];

            // Arrivee atteinte
            if (l == aLigne && c == aColonne) break;

            // Explorer les 4 voisins
            for (int i = 0; i < 4; i++) {
                int nl = l + deltas[i][0];
                int nc = c + deltas[i][1];

                // Hors limites
                if (nl < 0 || nl >= lignes || nc < 0 || nc >= cols) continue;

                Secteur voisin = grille[nl][nc];

                // Eviter l'eau
                if (voisin.EstEau()) continue;

                // Eviter les robots (sauf le secteur d'arrivee final)
                if (voisin.getRobot() != null && !(nl == aLigne && nc == aColonne)) continue;

                // Si la case n'a pas encore été visitée
                if (distance[nl][nc] == Integer.MAX_VALUE) {
                    distance[nl][nc] = distance[l][c] + 1; // voit pas trop.
                    directionPrise[nl][nc] = directionsMap[i]; // On mémorise la direction prise
                    file.add(new int[]{nl, nc});
                }
            }
        }

        // Pas de chemin trouve (la case d'arrivée est restée à l'infini)
        if (distance[aLigne][aColonne] == Integer.MAX_VALUE) {
            return new ArrayList<>();
        }

        // Reconstruction du chemin
        List<Directions> chemin = new ArrayList<>();
        int l = aLigne;
        int c = aColonne;

        // On remonte de l'arrivée vers le départ
        while (!(l == dLigne && c == dColonne)) {
            Directions dir = directionPrise[l][c];
            chemin.add(dir);

            // On déduit la case précédente en inversant le mouvement.
            if (dir == Directions.NORD)       l += 1;
            else if (dir == Directions.SUD)   l -= 1;
            else if (dir == Directions.EST)   c -= 1;
            else if (dir == Directions.OUEST) c += 1;
        }

        // Le chemin a été reconstruit à l'envers, on le remet dans le bon sens
        Collections.reverse(chemin);

        return chemin;
    }
}