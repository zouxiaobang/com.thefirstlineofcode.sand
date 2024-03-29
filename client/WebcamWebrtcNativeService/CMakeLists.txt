cmake_minimum_required(VERSION 3.21)

# set the project name and version
project(WebcamWebrtcNativeService VERSION 1.0)

# specify the C++ standard
set(CMAKE_CXX_STANDARD 14)
set(CMAKE_CXX_STANDARD_REQUIRED True)

set(CMAKE_CXX_VISIBILITY_PRESET hidden)
set(CMAKE_VISIBILITY_INLINES_HIDDEN 1)

if (CMAKE_HOST_WIN32)
	set(SOURCE_REPOS_DIR "C:\\Users\\49190\\source\\repos")
	set(VCPKG_DIR "${SOURCE_REPOS_DIR}\\vcpkg")
	
	set(CPPNET_SRC_DIR "${SOURCE_REPOS_DIR}\\CppNet_src")
	set(CPPNET_INCLUDE_DIR "${CPPNET_SRC_DIR}\\CppNet\\include")
	set(CPPNET_LIBRARY_PATH "${CPPNET_SRC_DIR}\\CppNet_vs_build\\lib\\Debug")
	
	set(WEBRTC_SRC_DIR "${SOURCE_REPOS_DIR}\\webrtc_native_code\\webrtc-checkout\\src")
	set(WEBRTC_INCLUDE_DIR "${WEBRTC_SRC_DIR};${WEBRTC_SRC_DIR}\\third_party\\abseil-cpp;${WEBRTC_SRC_DIR}\\third_party\\libyuv\\include")
	set(WEBRTC_LIBRARY_PATH "${WEBRTC_SRC_DIR}\\out\\Default\\obj")
	set(WEBRTC_VIDEO_CAPTURE_LIBRARY_PATH "${WEBRTC_LIBRARY_PATH}\\modules\\video_capture")
	set(JSONCPP_INCLUDE_DIR "${VCPKG_DIR}\\installed\\x64-windows-static\\include")
	set(JSONCPP_LIBRARY_DIR "${VCPKG_DIR}\\installed\\x64-windows-static\\debug\\lib")
	
	add_compile_definitions(USE_AURA=1)
	add_compile_definitions(_HAS_EXCEPTIONS=0)
	add_compile_definitions(__STD_C)
	add_compile_definitions(_CRT_RAND_S)
	add_compile_definitions(_CRT_SECURE_NO_DEPRECATE)
	add_compile_definitions(_SCL_SECURE_NO_DEPRECATE)
	add_compile_definitions(_ATL_NO_OPENGL)
	add_compile_definitions(_WINDOWS)
	add_compile_definitions(CERT_CHAIN_PARA_HAS_EXTRA_FIELDS)
	add_compile_definitions(PSAPI_VERSION=2)
	add_compile_definitions(WIN32)
	add_compile_definitions(_SECURE_ATL)
	add_compile_definitions(__WRL_NO_DEFAULT_LIB__)
	add_compile_definitions(WINAPI_FAMILY=WINAPI_FAMILY_DESKTOP_APP)
	add_compile_definitions(WINAPI_PARTITION_DESKTOP)
	add_compile_definitions(WIN10=_WIN32_WINNT_WIN10)
	add_compile_definitions(WIN32_LEAN_AND_MEAN)
	add_compile_definitions(NOMINMAX)
	add_compile_definitions(_UNICODE)
	add_compile_definitions(UNICODE)
	add_compile_definitions(NTDDI_VERSION=NTDDI_WIN10_RS2)
	add_compile_definitions(_WIN32_WINNT=0x0A00)
	add_compile_definitions(WINVER=0x0A00)
	add_compile_definitions(NDEBUG)
	add_compile_definitions(NVALGRIND)
	add_compile_definitions(DYNAMIC_ANNOTATIONS_ENABLED=0)
	add_compile_definitions(WEBRTC_ENABLE_PROTOBUF=0)
	add_compile_definitions(WEBRTC_INCLUDE_INTERNAL_AUDIO_DEVICE)
	add_compile_definitions(RTC_ENABLE_VP9)
	add_compile_definitions(HAVE_SCTP)
	add_compile_definitions(WEBRTC_LIBRARY_IMPL)
	add_compile_definitions(WEBRTC_NON_STATIC_TRACE_EVENT_HANDLERS)
	add_compile_definitions(WEBRTC_WIN)
	add_compile_definitions(ABSL_ALLOCATOR_NOTHROW=1)
	add_compile_definitions(HAVE_SCTP)
else()
	set(CPPNET_SRC_DIR "/home/pi/sources/CppNet_src")
	set(CPPNET_INCLUDE_DIR "${CPPNET_SRC_DIR}/CppNet/include")
	set(CPPNET_LIBRARY_PATH "${CPPNET_SRC_DIR}/CppNet_build/lib/Debug")
	
	set(WEBRTC_SRCDIR "/home/pi/sources/webrtc_native_code/webrtc-checkout/src")
	set(WEBRTC_INCLUDE_DIR "${WEBRTC_SRC_DIR};${WEBRTC_SRC_DIR}/third_party/abseil-cpp")
	set(WEBRTC_LIBRARY_PATH "${WEBRTC_SRC_DIR}/out/Default/obj")
	set(WEBRTC_VIDEO_CAPTURE_LIBRARY_PATH "${WEBRTC_LIBRARY_PATH}/modules/video_capture")
endif()

if(MSVC)
	add_compile_options(
		$<$<CONFIG:>:/MT> #---------|
		$<$<CONFIG:Debug>:/MTd> #---|-- Statically link the runtime libraries
		$<$<CONFIG:Release>:/MT> #--|
	)
endif()

# add the executable
add_executable(WebcamWebrtcNativeService
			SocketServer.cxx
			SocketServer.h
			vcm_capturer.cxx
			vcm_capturer.h
			test_video_capturer.cxx
			test_video_capturer.h
			WebcamWebrtcPeer.cxx
			WebcamWebrtcPeer.h
			WebcamWebrtcNativeService.cxx
)

target_include_directories(WebcamWebrtcNativeService
	PRIVATE ${CPPNET_INCLUDE_DIR}
	PRIVATE ${WEBRTC_INCLUDE_DIR}
	PRIVATE ${JSONCPP_INCLUDE_DIR}
	PRIVATE ${CMAKE_CURRENT_SOURCE_DIR}
)

find_library(CPPNET_LIB NAMES cppnet.lib PATHS ${CPPNET_LIBRARY_PATH})
find_library(WEBRTC_LIB NAMES webrtc.lib PATHS ${WEBRTC_LIBRARY_PATH})
find_library(WEBRTC_VIDEO_CAPTURE_LIB NAMES video_capture_module.lib PATHS ${WEBRTC_VIDEO_CAPTURE_LIBRARY_PATH})
find_library(JSONCPP_LIB NAMES jsoncpp.lib PATHS ${JSONCPP_LIBRARY_DIR})

target_link_libraries(WebcamWebrtcNativeService
	${CPPNET_LIB}
	${WEBRTC_LIB}
	${WEBRTC_VIDEO_CAPTURE_LIB}
	${JSONCPP_LIB}
	libucrtd.lib
	winmm.lib
	strmiids.lib
	secur32.lib
	msdmo.lib
	dmoguids.lib
	wmcodecdspuuid.lib
)

# add the install targets
if (CMAKE_HOST_WIN32)
	set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/bin)
	set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/bin)
	
	if (NOT CMAKE_BUILD_TYPE)
		set(CMAKE_BUILD_TYPE "Debug")
		message("Set CMAKE_BUILD_TYPE to default value(Debug). ")
	endif()
	
	install(TARGETS WebcamWebrtcNativeService
			DESTINATION bin
			)
else()
	install(TARGETS WebcamWebrtcNativeService
			DESTINATION bin
			)
endif()
