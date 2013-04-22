<?php

   function obtieneImagenes($opciones){
   	$link = mysql_connect("localhost", "root","root"); 
      mysql_select_db("turismo", $link); 
      $result = mysql_query($opciones, $link);
      
      $cont = 0;
	  while ($row = mysql_fetch_row($result)){ 
         $cat[$cont] = array("nombreImagen" =>utf8_encode($row[0]),"latitud" => $row[1], "longitud" => $row[2], "idCategoria" => $row[4], "comentario" => $row[3], "nombreCategoria"=> utf8_encode($row[5] ));
		   $cont++;
      }
	 $listaImagenes = array("imagenes"=>$cat);
     return $listaImagenes;   	
     
   }
   
	function recibeImaArreglo($nombreImagen, $contenido, $latitud, $longitud, $comentario, $categoria, $width, $heigth){
		
	   $cant = count($contenido);
		header('Content-Type: image/jpeg');
		
      $cadena = "";
		for ($i=0; $i<$cant; $i++){
		   $cadena .= $contenido[$i];
		}
		
		$data = base64_decode($cadena);
	   $file = $nombreImagen.".jpg";
	   $success = file_put_contents($file, $data);	   		
	   
	   return "cantidad ".$cant;
/*		
		// binary, utf-8 bytes
   	$binary=base64_decode($cadena);
   	

//activar la biblioteca de funciones gd

		$img = imagecreatefromstring($binary);
		
		if($img != false)
		{
   		imagejpeg($img,$nombreImagen."jpg");
   		return "recibido";
		}
		else {
		   return "error";	
		}					
	
		
		 imagedestroy($img);
*/	
		/*
		$data = base64_decode($cadena);
	   $file = $nombreImagen.'.jpg';
	   $success = file_put_contents($file, $data);
	   return "cantidad:".$cant;
	   
	   */
		/*
		$cant = count($contenido);
	$x = 0;
	$y = 0;
	


 // Create new image with desired dimensions
  $image = imagecreatetruecolor($width, $heigth) or die("No se puede crear");
	for($i=0; $i<$cant; $i++){
		$binary=base64_decode($contenido[$i]);
		$imag = imagecreatefromstring($binary);
		$w = ImageSX($imag); // width
		$h = ImageSY($imag); // height
		$arrImag[$i] = $imag;
		//imagejpeg($imag, "prueba".$i."jpg");
	}
		$i = 0;
		for ($j=0; $j<4; $j++){
			for($k=0; $k<4; $k++){
				imagecopy($image, $arrImag[$i], $k*$w, $j*$h, 0, 0, $w, $h);
				$i++;
			}
		}

  
	 imagejpeg($image, $nombreImagen."jpg");
 // Clean up
	unset($arrImag);
 imagedestroy($image);
 imagedestroy($imag);
 return "cantidad:".$cant	;
*/
	}

   function recibeImagen($nombreImagen, $latitud, $longitud, $comentario,$idCategoria) {
  		
   		$link = mysql_connect("localhost", "root","root"); 
      	mysql_select_db("turismo", $link); 
      	$result = mysql_query("insert into imagen values('".$nombreImagen."',".$latitud.",".$longitud.",'".$comentario."',".$idCategoria.");", $link);
      	 
   		return "Insertado";
	
   }
   /*
      function recibeImagen($imagen, $nombreIm) {
   	
   	$binary=base64_decode($imagen);

		// binary, utf-8 bytes

		header('Content-Type: image/jpeg');
//activar la biblioteca de funciones gd

		$img = imagecreatefromstring($binary);
		
		if($img != false)
		{
   		imagejpeg($img,$nombreIm);
   		return "recibido";
		}
		else {
		   return "error";	
		}			
		

   }
   */
   function obtieneCategorias() {
   	
	   	
   	
		$link = mysql_connect("localhost", "root","root"); 
      mysql_select_db("turismo", $link); 
      $result = mysql_query("select idCategoria, nombreCategoria from categoria", $link); 
    
	  $cont = 0;
	  while ($row = mysql_fetch_row($result)){ 
         $cat[$cont] = array("idCategoria" =>utf8_encode($row[0]),"nombreCategoria" => utf8_encode($row[1]));
		   $cont++;
      }
	 $listaCategorias = array("categoria"=>$cat);
     return $listaCategorias;   	
   }
   
   
   ini_set("soap.wsdl_cache_enabled","0");
   $server = new SoapServer("http://localhost/pags/servicios/servicios.wsdl");
   
   $server->addFunction("enviaImagen");
   $server->addFunction("recibeImagen");
   $server->addFunction("obtieneCategorias");
   $server->addFunction("recibeImaArreglo");
   $server->addFunction("obtieneImagenes");   
   
   $server -> handle();
 
?>