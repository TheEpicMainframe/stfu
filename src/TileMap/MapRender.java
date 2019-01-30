package TileMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import Resources.GameResources;

import poj.Render.ImageRenderObject;
import poj.Render.ImageWindow;
import poj.Render.Renderer;
import poj.linear.Matrix;
import poj.linear.MatrixCord;

/*
 * packed data is matrix.. for map
 * change container
 */

public class MapRender extends Matrix<Integer>
{
	private ArrayList<Matrix<Integer>> mapLayers =
		new ArrayList<Matrix<Integer>>();
	private int rowsOfTileSet, colsOfTileSet, tileHeight, tileWidth,
		mapTileWidth, mapTileHeight;

	private ArrayList<Integer> mapLayer = new ArrayList<Integer>();

	public void addMapConfig(String mapConfigLocation)
		throws FileNotFoundException
	{
		Scanner configReader = new Scanner(new File(mapConfigLocation));
		String tempString[];
		tempString = configReader.nextLine().split("\"");
		/*
		mapHeight = Integer.parseInt(
			tempString[2].substring(1, tempString[2].length() - 1));
			*/
		while (configReader.hasNextLine()) {
			tempString = configReader.nextLine().split("\"");
			if (tempString.length > 1) {
				if (tempString[1].equals("tileheight")) {
					mapTileHeight = Integer.parseInt(
						tempString[2].substring(
							1,
							tempString[2].length()
								- 1));
				}
				if (tempString[1].equals("tilewidth")) {
					mapTileWidth = Integer.parseInt(
						tempString[2].substring(
							1,
							tempString[2].length()
								- 1));
				}
				/*
			if (tempString[1].equals("width")) {
				mapWidth = Integer.parseInt(
					tempString[2].substring(
						1,
						tempString[2].length()
							- 1));
				System.out.println("mapWidth ="
						   + mapWidth);
				break;
			}
			*/
			}
		}
		configReader.close();
	}

	public void addMapLayer(String mapLayerLocation)
		throws FileNotFoundException
	{
		Scanner mapReader = new Scanner(new File(mapLayerLocation));
		Matrix<Integer> tempLayer;
		// ArrayList<Integer> tempIntList = new ArrayList<Integer>();
		int numRows = 0;
		while (mapReader.hasNextLine()) {
			++numRows;
			String line = mapReader.nextLine();
			String tempList[] = line.split(",");
			for (int i = 0; i < tempList.length; ++i) {
				tempIntList.add(Integer.parseInt(tempList[i]));
			}
		}

		tempLayer = new Matrix<Integer>(tempIntList, numRows);
		mapLayers.add(tempLayer);
		mapReader.close();
	}

	public void addTileSet(String tileLocation) throws FileNotFoundException
	{
		Scanner mapReader = new Scanner(new File(tileLocation));
		while (mapReader.hasNextLine()) {
			String line = mapReader.nextLine();
			String tempList[] = line.split("\"");
			if (tempList.length > 1) {
				switch (tempList[1]) {
				case "columns":
					colsOfTileSet = Integer.parseInt(
						tempList[2].substring(
							1, tempList[2].length()
								   - 1));
					break;
				// case "imageheight":
				// break;
				// case "imagewidth":
				// break;
				case "tilecount":
					rowsOfTileSet =
						Integer.parseInt(
							tempList[2].substring(
								1,
								tempList[2].length()
									- 1))
						/ colsOfTileSet;
					break;
				case "tileheight":
					tileHeight = Integer.parseInt(
						tempList[2].substring(
							1, tempList[2].length()
								   - 1));
					break;
				case "tilewidth":
					tileWidth = Integer.parseInt(
						tempList[2].substring(
							1, tempList[2].length()
								   - 1));
					break;
				}
			}
		}
		mapReader.close();
	}
	public void renderMap(Renderer renderer)
	{
		// rendering tile maps loop
		for (int i = 0; i < mapLayers.size(); ++i) {
			int curLayerRows = mapLayers.get(i).rows,
			    curLayerCols = mapLayers.get(i).cols,
			    // curTilesetRows = map.rowsOfTileSet,
				curTilesetCols = colsOfTileSet,
			    curTilesetHeight = tileHeight,
			    curTilesetWidth = tileWidth;
			for (int j = 0; j < (curLayerRows) * (curLayerCols);
			     ++j) {
				int valueOfTile = getTileFromMap(i, j);
				int xShiftValue = 0,
				    yShiftValue = -mapTileHeight * 3 / 2;
				// TODO please don't delete the debug
				// message until the render for tile map
				// is set in stone!!!

				/*
				System.out.println("J = " + j);
				System.out.println("valueOfTile = "
						   + valueOfTile);
				System.out.println("curTilesetHeight = "
						   + curTilesetHeight);
				System.out.println("curTilesetWidth = "
						   + curTilesetWidth);
				System.out.println("j / curLayerCols ="
						   + j / curLayerCols
							     * 64);
				System.out.println("j % curLayerCols ="
						   + j % curLayerCols
							     * 64);
				System.out.println(
					"valueOfTile / curTilesetCols ="
					+ valueOfTile / curTilesetCols);
				System.out.println(
					"valueOfTile % curTilesetCols ="
					+ valueOfTile % curTilesetCols);
				System.out.println(
					"(j - curLayerCols) = "
					+ (j - curLayerCols));
				System.out.println(
					"(j - curLayerCols)/curLayerRows
				="
					+ (j - curLayerCols)
						  / curLayerCols);
				System.out.println(
					"(j
				-curLayerRows)/curLayerCols%2 = "
					+ ((j - curLayerCols)
					   / curLayerCols)
						  % 2);
				System.out.println("curLayerRows ="
						   + curLayerRows);
				System.out.println("xShiftValue= "
						   + xShiftValue);
				System.out.println("this bsv.."
						   + ((j - curLayerRows)
						      / curLayerCols));
				System.out.println(
					"((j - curLayerRows) /
				curLayerCols )% 2 = "
					+ ((j - curLayerRows)
					   / curLayerCols)
						  % 2);
				System.out.println("J/curLayerCols= "
						   + j / curLayerCols);
				System.out.println(
					"yshift bs ="
					+ (yShiftValue
					   * (j / curLayerCols)));
				*/
				if (((j - curLayerCols) / curLayerCols) % 2 == 0
				    && j >= curLayerCols) {
					xShiftValue = mapTileWidth / 2;
				}
				if (j < curLayerCols) {
					yShiftValue = 0;
				}
				if (valueOfTile != -1) {
					renderer.pushRenderObject(new ImageRenderObject(
						j % curLayerCols
								* curTilesetWidth
							+ xShiftValue,
						(j / curLayerCols
						 * curTilesetHeight)
							+ yShiftValue
								  * (j
								     / curLayerCols),

						GameResources.testTile,
						new ImageWindow(
							valueOfTile
								% curTilesetCols
								* curTilesetWidth,
							valueOfTile
								/ curTilesetCols
								* curTilesetHeight,
							curTilesetWidth,
							curTilesetHeight)));
				}
			}
		}
	}
	public int getTileFromMap(int layerNumber, int index)
	{
		return mapLayers.get(layerNumber).getDataWithIndex(index);
	}

	public int getTileFromMap(int layerNumber, MatrixCord cord)
	{
		return mapLayers.get(layerNumber).getDataWithMatrixCord(cord);
	}


	public void printMapLayers()
	{
		for (int i = 0; i < mapLayers.size(); ++i) {
			for (int j = 0; j < mapLayers.get(i).rows; ++j) {
				System.out.println("row " + j);
				for (int k = 0; k < mapLayers.get(i).cols;
				     ++k) {
					System.out.print(
						mapLayers.get(i).getDataWithIndex(
							mapLayers.get(i).cols
								* j
							+ k)
						+ ", ");
				}
				System.out.println();
			}
		}
	}
}

/*
 *
 */
