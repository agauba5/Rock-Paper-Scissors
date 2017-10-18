package edu.cnm.deepdive.ca.RPS.models;

import java.util.Random;

public class Terrain {

  private Breed[][] cells;
  private Random rng = null;
  private int size;
  private Neighborhood neighborhood;

  public Terrain(){

  }
  public void initialize(){
    Breed[] breeds= Breed.values();
    cells = new Breed[size][size];
    for(int i = 0; i < size; i++){
      for(int j = 0; j < size; j++){
        cells[i][j] = breeds[getRng().nextInt(breeds.length)];
      }
    }
  }

  public Random getRng() {
    if(rng == null){
      new Random();
    }
    return rng;
  }

  public void setRng(Random rng) {
    this.rng = rng;
  }


  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public Neighborhood getNeighborhood() {
    return neighborhood;
  }

  public void setNeighborhood(Neighborhood neighborhood) {
    this.neighborhood = neighborhood;
  }
  public void combat(){
    int[] cell = randomCell();
    int[] neighbor = randomNeighbor(cell[0],cell[1]);
    Breed attacker = cells[cell[0]][cell[1]];
    Breed defender = cells[neighbor[0]][neighbor[1]];
    int outcome = Breed.comparator.compare(attacker,defender);
    if(outcome > 0){
      cells[neighbor[0]][neighbor[1]] = attacker;
    }else if(outcome< 0){
      cells[cell[0]][cell[1]] = defender;
    }
  }

  private int[] randomCell(){
    return new int[] {
        getRng().nextInt(size),
        getRng().nextInt(size)
    };
  }
  private int[] randomNeighbor(int row, int column){
    int[] offset = neighborhood.randomNeighbor(getRng());
    return new int[] {
        (size+ row + offset[0]) % size ,
        (size+ column + offset[1]) % size
    };
  }
  public enum Neighborhood{
    Moore {
      {
        neighbors = new int[][]{
            {-1,-1},{0,-1},{1,-1},
            {-1, 0},       {1, 0},
            {-1, 1},{0, 1},{1, 1}
        };
      }
    },
    VON_NEUMAN {
      {
        neighbors = new int[][]{
                    {0,-1},
            {-1, 0},       {1, 0},
                    {0, 1}
        };
      }
    };

    protected int[][] neighbors;

    public int[] randomNeighbor(Random rng){
      return neighbors[rng.nextInt(neighbors.length)];

    }
  }
}