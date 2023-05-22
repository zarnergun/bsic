import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Surface
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.zam.bsic.R

class QrcodeFragment : Fragment() {

    private lateinit var cameraContainer: FrameLayout
    private lateinit var surfaceView: SurfaceView
    private lateinit var cameraManager: CameraManager
    private lateinit var cameraDevice: CameraDevice
    private lateinit var previewRequestBuilder: CaptureRequest.Builder
    private var previewSession: CameraCaptureSession? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_qrcode, container, false)
        cameraContainer = view.findViewById(R.id.camera_container)

        // Demander les permissions caméra
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), 0)
        }

        // Créer le SurfaceView et l'ajouter au conteneur
        surfaceView = SurfaceView(context)
        cameraContainer.addView(surfaceView)

        // Obtenir CameraManager et le ID de la caméra frontale
        cameraManager = activity?.getCameraManager()
        val frontCameraId = cameraManager?.cameraIdToInfo?.keys?.find { it.startsWith(
            CameraCharacteristics.LENS_FACING_FRONT) }

        // Ouvrir la caméra et démarrer le preview
        val imageReader = ImageReader.newInstance(1024, 1024, ImageFormat.JPEG, 2)
        previewRequestBuilder = cameraManager?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        previewRequestBuilder?.addTarget(surfaceView.holder.surface)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        cameraManager?.openCamera(frontCameraId, object: CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                cameraDevice = camera
                createPreviewSession()
            }
        }, null)

        return view
    }

    private fun createPreviewSession() {
        // Créer la session de capture prévisualisée
        cameraDevice.createCaptureSession(
            Arrays.asList(surfaceView.holder.surface, imageReader.surface),
            object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    previewSession = session
                    previewSession?.setRepeatingRequest(previewRequestBuilder.build(), null, null)
                }
            }, null)
    }

    override fun onResume() {
        super.onResume()
        if (cameraManager != null) {
            if (cameraDevice == null) {
                cameraManager.openCamera(cameraManager.cameraIdToInfo.keys.find {
                    it.startsWith(CameraCharacteristics.LENS_FACING_FRONT) }, this, null)
            } else {
                previewSession?.setRepeatingRequest(previewRequestBuilder.build(), null, null)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        previewSession?.close()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraDevice.close()
    }
}
