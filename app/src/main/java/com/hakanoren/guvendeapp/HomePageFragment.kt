package com.hakanoren.guvendeapp
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.hakanoren.guvendeapp.databinding.FragmentHomePageBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import androidx.recyclerview.widget.RecyclerView

// Konum izinleri
import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.hakanoren.guvendeapp.models.Contact


private var torchState: Boolean = false
private lateinit var cameraManager: CameraManager
private var camId: String = "0"
private var _binding: FragmentHomePageBinding? = null
private val binding get() = _binding!!
private var mediaPlayer: MediaPlayer? = null


private lateinit var fusedLocationClient: FusedLocationProviderClient


class HomePageFragment : Fragment() {
    private var isBlinking: Boolean = false
    private val handler = Handler()
    private var isPlayingFakeSound: Boolean = false
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ContactAdapter
    private val contactList = mutableListOf<Contact>()
    private lateinit var viewModel: ContactViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Binding tanımlama.
        _binding = FragmentHomePageBinding.inflate(inflater, container, false)

        // FusedLocationProviderClient'i başlat.
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        viewModel = ViewModelProvider(requireActivity()).get(ContactViewModel::class.java)

        // RecyclerView ayarları.
        recyclerView = binding.recyclerView
        adapter = ContactAdapter(contactList) { position ->

        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        requestPermissions()

        loadContacts()

        setupCameraAndPermissions()
        setupCallButton()
        setupNormalCallButton()
        setupLocationButton()
        setupSendAllMessagesButton()
        setupPlaySoundButton()
        return binding.root
    }

    // Tüm izinleri isteyen fonksiyon.
    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CALL_PHONE
        )

        ActivityCompat.requestPermissions(requireActivity(), permissions, 1)
    }


    private fun loadContacts(): List<String> {
        val contactsList = mutableListOf<Contact>() // Arka planda çekilen kişilerin listesi.
        val sharedPreferences = requireContext().getSharedPreferences("contacts", Context.MODE_PRIVATE)
        val allContacts = sharedPreferences.all

        for (entry in allContacts) {
            val parts = entry.key.split(":")
            if (parts.size == 2) {
                val contact = Contact(parts[0], parts[1])
                contactsList.add(contact) // Kişileri arka planda ekliyoruz.
            }

        }
        return contactsList.map { it.phoneNumber }
    }


    // ------------------------------KONUM MESAJI GONDERME-------------------------------

    private fun sendLocationMessage() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // İzinleri iste
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity(), OnSuccessListener<Location> { location ->

            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                val phoneNumbersToSend = loadContacts()
                val message = "Buradayım! Lütfen gelin yardıma ihtiyacım var!: https://www.google.com/maps/search/?api=1&query=$latitude,$longitude"
                Log.d("Location", "Latitude: $latitude, Longitude: $longitude") // Konum bilgilerini logla
                sendSMS(phoneNumbersToSend.toString(), message) // Göndermek istediğiniz numaralar
            } else {
                Log.d("Location", "Konum bilgisi alınamadı. İzin verdiğinizden ve internetinizin açık olduğundan emin olun.")
            }
        })
    }

    private fun sendSMS(phoneNumber: String, message: String) {
        if (message.isNotEmpty()) { // Mesajın boş olmadığını kontrol et
            val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:$phoneNumber") // SMS uygulamasını aç
                putExtra("sms_body", message) // Mesaj içeriği
            }
            startActivity(smsIntent) // SMS uygulamasını aç
        } else {

        }
    }



    private fun setupLocationButton() {
        binding.callButton7.setOnClickListener {
            sendLocationMessage()
        }
    }

    // ------------------------------GRUP ACIL YARDIM MESAJI GONDERME-------------------------------

    private fun sendNormalSMS(phoneNumbers: List<String>, message: String) {
        if (message.isNotEmpty()) {
            val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:${phoneNumbers.joinToString(",")}") // Tüm numaraları birleştirerek kullan
                putExtra("sms_body", message)
            }
            startActivity(smsIntent)
        } else {
            Log.d("SMS", "Gönderilecek mesaj boş.")
        }
    }

    private fun setupSendAllMessagesButton() {
        binding.callButton5.setOnClickListener {
            sendMessagesToAll()
        }
    }


    private fun sendMessagesToAll() {

        val message = "Tehlikede olabilirim, bu mesajı görüyorsanız lütfen benim için tetikte olun."
        val phoneNumbersToSend = loadContacts()

        sendNormalSMS(phoneNumbersToSend, message)
    }


    // ----------------------------KAMERA IZINLERI---------------------------------

    private fun setupCameraAndPermissions() {

        cameraManager = requireContext().getSystemService(Context.CAMERA_SERVICE) as CameraManager
        camId = cameraManager.cameraIdList[0] // Arka kamera

        // Dexter ile kamera izni kontrolü
        Dexter.withContext(requireContext())
            .withPermission(android.Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {

                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    // İzin verildiyse, buton işlevini tanımla.
                    setupButton()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Toast.makeText(
                        requireActivity(),
                        "Lütfen erişime izin verin.",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            }).check()
    }

    private fun setupButton() {
        binding.callButton4.setOnClickListener {
            when {
                !torchState -> {
                    // İlk tıklamada flaşı aç
                    cameraManager.setTorchMode(camId, true)
                    torchState = true
                }
                torchState && !isBlinking -> {
                    // İkinci tıklamada flaşı kapat ve yanıp sönme moduna geç
                    isBlinking = true
                    startBlinking()
                }
                else -> {
                    // Yanıp sönme modunda tıklanırsa flaş'ı kapat
                    stopBlinking()
                    torchState = false
                    isBlinking = false
                }
            }
        }
    }




    // ------------------------------POLIS TELSIZ SESI CALMA-------------------------------

    private fun setupPlaySoundButton() {
        binding.callButton1.setOnClickListener {
            toggleSound() // Ses çalma fonksiyonunu çağır
        }
    }

    private fun playFakeSound() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(requireContext(), R.raw.police_fake_radio) // Ses dosyanızı burada tanımlayın
            mediaPlayer?.isLooping = true // Sesin döngüde çalmasını istiyorsanız
        }
        mediaPlayer?.start()
    }

    private fun toggleSound() {
        if (isPlayingFakeSound) {
            stopSound() // Eğer ses çalıyorsa durdur
            isPlayingFakeSound = false
        } else {
            playFakeSound() // Eğer ses çalmıyorsa çalmaya başla
            isPlayingFakeSound = true
        }
    }

    // ------------------------------NORMAL ARAMA-------------------------------

    private fun setupNormalCallButton() {
        binding.callButton3.setOnClickListener {
            val phoneNumbersToCall = loadContacts()
            makePhoneCall(phoneNumbersToCall[0]) // Eklenen kişi listesindeki ilk kişiyi arayacak
        }
    }

    private fun makeNormalPhoneCall(phoneNumber: String) {
        // Telefon araması için bir intent oluşturun
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:$phoneNumber")

        // Arama yapmak için izin kontrolü yapın
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // İzin verilmemişse, izin isteyin
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CALL_PHONE), 1)
        } else {
            // İzin verilmişse, aramayı başlatın
            startActivity(callIntent)
        }
    }

    // ------------------------------ACIL SERVIS ARAMA-------------------------------

    private fun setupCallButton() {
        binding.callButton6.setOnClickListener {
            val phoneNumber = "112" // Aramak istediğiniz numara
            makePhoneCall(phoneNumber)
        }
    }

    private fun makePhoneCall(phoneNumber: String) {
        // Telefon araması için bir intent oluşturun
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:$phoneNumber")

        // Arama yapmak için izin kontrolü yapın
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // İzin verilmemişse, izin isteyin
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CALL_PHONE), 1)
        } else {
            // İzin verilmişse, aramayı başlatın
            startActivity(callIntent)
        }
    }
// ---------------------------------------------------------------------------

    private fun startBlinking() {
        handler.post(object : Runnable {
            override fun run() {
                cameraManager.setTorchMode(camId, !torchState) // Flaşın durumunu değiştir
                torchState = !torchState // Durumu güncelle
                handler.postDelayed(this, 500) // 1000 ms aralıkla tekrarla
                //binding.callButton4.setImageResource(R.drawable.flash_light_alert) // İkonu değiştir
                playSound()
            }
        })
    }

    private fun playSound() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(requireContext(), R.raw.alert_sound) // Ses dosyanızı burada tanımlayın
            mediaPlayer?.isLooping = true // Sesin döngüde çalmasını istiyorsanız
        }
        mediaPlayer?.start()
    }

    private fun stopSound() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun stopBlinking() {
        isBlinking = false
        handler.removeCallbacksAndMessages(null) // Yanıp sönme işlemini durdur
        cameraManager.setTorchMode(camId, false) // Flaş kapat
        //binding.callButton4.setImageResource(R.drawable.homepage_button_styles) // İkonu değiştir
        torchState = false
        stopSound()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        stopSound()
    }
}
