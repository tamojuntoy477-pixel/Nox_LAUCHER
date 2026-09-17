package net.kdt.pojavlaunch.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;

public class ProfileTypeSelectFragment extends Fragment {
    public static final String TAG = "ProfileTypeSelectFragment";

    public ProfileTypeSelectFragment() {
        super(R.layout.fragment_profile_type);
    }

    public ProfileTypeSelectFragment(int layout) {
        super(layout);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.vanilla_profile).setOnClickListener(v -> Tools.swapFragment(requireActivity(), ProfileEditorFragment.class,
                ProfileEditorFragment.TAG, new Bundle(1)));

        // Keep these transitions out of the back stack because the installer fragments
        // expect to return directly to the main launcher after finishing.
        view.findViewById(R.id.optifine_profile).setOnClickListener(v ->
                openInstaller(OptiFineInstallFragment.class, OptiFineInstallFragment.TAG));
        view.findViewById(R.id.modded_profile_fabric).setOnClickListener(v ->
                openInstaller(FabricInstallFragment.class, FabricInstallFragment.TAG));
        view.findViewById(R.id.modded_profile_forge).setOnClickListener(v ->
                openInstaller(ForgeInstallFragment.class, ForgeInstallFragment.TAG));
        view.findViewById(R.id.modded_profile_neoforge).setOnClickListener(v ->
                openInstaller(NeoForgeInstallFragment.class, NeoForgeInstallFragment.TAG));
        view.findViewById(R.id.modded_profile_modpack).setOnClickListener(v ->
                openInstaller(ModpackCreateFragment.class, ModpackCreateFragment.TAG));
        view.findViewById(R.id.modded_profile_lwjgl3ify).setOnClickListener(v ->
                openInstaller(LWJGL3ifyInstallFragment.class, LWJGL3ifyInstallFragment.TAG));
        view.findViewById(R.id.modded_profile_quilt).setOnClickListener(v ->
                openInstaller(QuiltInstallFragment.class, QuiltInstallFragment.TAG));
        view.findViewById(R.id.modded_profile_bta).setOnClickListener(v ->
                openInstaller(BTAInstallFragment.class, BTAInstallFragment.TAG));
    }

    private void openInstaller(Class<? extends Fragment> fragmentClass, String tag) {
        Tools.swapFragment(requireActivity(), fragmentClass, tag, null);
    }
}
