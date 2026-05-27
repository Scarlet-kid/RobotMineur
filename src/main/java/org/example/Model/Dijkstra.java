package org.example.Model;

import org.example.Model.Directions;
import org.example.Model.Secteur;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class Dijkstra {

    public static List<Directions> calculerChemin(Secteur[][] grille, Secteur depart, Secteur arrivee) {

        int lignes = grille.length;
        int cols   = grille[0].length;

        // Distance minimale depuis le depart
        int[][] distance = new int[lignes][cols];
        // Pour reconstruire le chemin a la fin
        int[][] parentLigne   = new int[lignes][cols];
        int[][] parentColonne = new int[lignes][cols];

        for (int[] row : distance)       Arrays.fill(row, Integer.MAX_VALUE);
        for (int[] row : parentLigne)    Arrays.fill(row, -1);
        for (int[] row : parentColonne)  Arrays.fill(row, -1);

        int dLigne   = depart.getLigne();
        int dColonne = depart.getColonne();
        int aLigne   = arrivee.getLigne();
        int aColonne = arrivee.getColonne();

        distance[dLigne][dColonne] = 0;

        // File de priorite : [distance, ligne, colonne]
        PriorityQueue<int[]> file = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        file.add(new int[]{0, dLigne, dColonne});

        // Les 4 directions
        int[][] deltas = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}};

        while (!file.isEmpty()) {
            int[] actuel = file.poll();
            int dist = actuel[0];
            int l    = actuel[1];
            int c    = actuel[2];

            // Arrivee atteinte
            if (l == aLigne && c == aColonne) break;

            // Distance obsolete
            if (dist > distance[l][c]) continue;

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

                int nouvelleDist = distance[l][c] + 1;

                if (nouvelleDist < distance[nl][nc]) {
                    distance[nl][nc]      = nouvelleDist;
                    parentLigne[nl][nc]   = l;
                    parentColonne[nl][nc] = c;
                    file.add(new int[]{nouvelleDist, nl, nc});
                }
            }
        }

        // Pas de chemin trouve
        if (distance[aLigne][aColonne] == Integer.MAX_VALUE) {
            return new ArrayList<>();
        }

        // Reconstruction du chemin
        List<Directions> chemin = new ArrayList<>();
        int l = aLigne;
        int c = aColonne;

        while (!(l == dLigne && c == dColonne)) {
            int pl = parentLigne[l][c];
            int pc = parentColonne[l][c];

            if (l == pl - 1)      chemin.add(0, Directions.NORD);
            else if (l == pl + 1) chemin.add(0, Directions.SUD);
            else if (c == pc + 1) chemin.add(0, Directions.EST);
            else                  chemin.add(0, Directions.OUEST);

            l = pl;
            c = pc;
        }

        return chemin;
    }
}