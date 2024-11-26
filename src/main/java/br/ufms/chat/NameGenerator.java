package br.ufms.chat;

public class NameGenerator {
    private static String[] names = {
            "Francis Gustavo", "Edmilson", "Alex Junior", "Alex", "Edevaldo", "Samuel", "Cladson",
            "Herminio", "Emerson", "Andrei", "Ronaldo", "Manoel", "Ademir", "Delmo", "Fabiano", "Anderson"
    };

    private static String[] surnames = {
            "Dezembro", "Gonçalves", "da Silva", "Correia de Lima", "Ferreira", "Thiago Fraga", "Rodolfo",
            "Alfonso Orozco", "Andrei Scoparo", "Manoel Dias", "Barbosa", "Candido", "Roberto", "Vieira", "de Oliviera"
    };

    public static String generateName() {
        return names[(int) (Math.random() * names.length)] + " "+surnames[(int) (Math.random() * surnames.length)];
    }
}
