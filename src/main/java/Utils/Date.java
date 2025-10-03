package Utils;

public class Date {
    //Instances
    private int jour;
    private int mois;
    private int année;
    private int heure;
    private int minute;

    //Constructeur

    public Date(Builder builder) throws InvalidDate{
            this.jour = builder.jour;
            this.mois = builder.mois;
            this.année = builder.année;
            this.heure = builder.heure;
            this.minute = builder.minute;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Date date = (Date) obj;
        return jour == date.jour &&
                mois == date.mois &&
                année == date.année &&
                heure == date.heure &&
                minute == date.minute;
    }


    //Innerclass tout pareil que restaurant
    public static class Builder {
        private int jour;
        private int mois;
        private int année;
        private int heure;
        private int minute;

        public Builder jour(int jour) throws InvalidDate {
            if (jour < 1 || jour > 31) {
                throw new InvalidDate("Le jour n'existe pas");
            }
            this.jour = jour;
            return this;
        }

        public Builder mois(int mois) throws InvalidDate {
            if (mois < 1 || mois > 12) {
                throw new InvalidDate("Le mois n'existe pas");
            }
            this.mois = mois;
            return this;
        }

        public Builder année(int année) {
            this.année = année;
            return this;
        }

        public Builder heure(int heure) throws InvalidDate {
            if (heure < 0 || heure > 23) {
                throw new InvalidDate("L'heure est incompatible");
            }
            this.heure = heure;
            return this;
        }

        public Builder minute(int minute) throws InvalidDate {
            if (minute < 0 || minute > 59) {
                throw new InvalidDate("La minute est incompatible");
            }
            this.minute = minute;
            return this;
        }

        public Date build() {
            return new Date(this);
        }
    }

}
